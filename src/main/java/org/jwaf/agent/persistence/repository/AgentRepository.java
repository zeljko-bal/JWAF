package org.jwaf.agent.persistence.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.exceptions.AgentNotFound;
import org.jwaf.agent.exceptions.AgentStateChangeFailed;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentEntityView;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.data.mongo.annotations.MorphiaAdvancedDatastore;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;

import com.mongodb.DuplicateKeyException;

/**
 * A repository bean that contains methods for crud operations on {@link AgentEntity} 
 * and for transactions that change agent's state.
 */
@Stateless
@LocalBean
public class AgentRepository 
{
	private static final int MAX_RETRIES = 50;
	
	@Inject @MorphiaAdvancedDatastore
	private AdvancedDatastore ds;
	
	@Inject
	private Logger log;
	
	// crud
	
	public AgentEntity findAgent(String agentName)
	{
		return find(agentName);
	}
	
	public AgentEntityView findView(String agentName)
	{
		AgentEntityView ret = basicQuery(agentName)
				.retrievedFields(true, "aid", "type", "state")
				.get();
		
		if(ret == null)
		{
			throw new AgentNotFound();
		}
		else
		{
			return ret;
		}
	}
	
	public void create(AgentEntity agent)
	{
		ds.insert(agent);
	}
	
	public AgentEntity remove(String agentName)
	{
		return ds.findAndDelete(basicQuery(agentName));
	}
	
	// activation
	
	public boolean activateSingleThreaded(String agentName)
	{
		return activateSingleThreaded(agentName, null);
	}
	
	public boolean activateSingleThreaded(String agentName, ACLMessage message)
	{
		// try at most MAX_RETRIES times
		for(int i=0; i<MAX_RETRIES; i++)
		{
			// retrieve previous state and hasNewMessages flag
			AgentEntity agent = basicQuery(agentName)
					.retrievedFields(true, "state", "hasNewMessages")
					.get();
			
			assertAgentExists(agent, agentName);
			
			String prevState = agent.getState();
			boolean alreadyHasMessages = agent.hasNewMessages();
			boolean newMessageAvailable = message != null;
			boolean activated = false;
			
			Query<AgentEntity> query = basicQuery(agentName)
					.field("state").equal(prevState);
			
			UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class);
			
			if(newMessageAvailable)
			{
				updates.add("messages", message);
			}
			
			// if prevState was PASSIVE
			if(AgentState.PASSIVE.equals(prevState))
			{
				if(alreadyHasMessages || newMessageAvailable)
				{
					if(!newMessageAvailable)
					{
						// if no new messages available, ensure that we already have messages waiting
						query.field("hasNewMessages").equal(true);
					}
					
					// activate agent
					updates.set("state", AgentState.ACTIVE)
							.set("activeInstances", 1);
					
					activated = true;
				}
				else
				{
					// no messages waiting, no reason to activate
					return false;
				}
			}
			
			// if agent is about to be activated
			if(activated)
			{
				updates.set("hasNewMessages", false);
			}
			else if(newMessageAvailable)
			{
				updates.set("hasNewMessages", true);
			}
			
			UpdateResults res = ds.update(query, updates);
			
			// if updated return, else retry
			if(res.getUpdatedCount() > 0)
			{
				return activated;
			}
			else
			{
				log.warn("Activation of agent <{}> failed due to state change, try count {}, will retry at most {} times."
						+ " Expected: {}"
						,agentName, i+1, MAX_RETRIES, query);
			}
		}
		
		// if unsuccessful after MAX_RETRIES times
		log.error("Activation of agent <{}> failed due to state change after {} retries, aborting.", agentName);
		throw new AgentStateChangeFailed("Unable to activate agent.");
	}
	
	public boolean passivateSingleThreaded(String agentName)
	{
		// try at most MAX_RETRIES times
		for(int i=0; i<MAX_RETRIES; i++)
		{
			// retrieve hasNewMessages flag
			AgentEntity agent = basicQuery(agentName)
					.retrievedFields(true, "hasNewMessages")
					.get();
			
			assertAgentExists(agent, agentName);
			
			boolean hasNewMessages = agent.hasNewMessages();
			
			Query<AgentEntity> query = basicQuery(agentName)
					.field("hasNewMessages").equal(hasNewMessages);
			
			UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class);
			
			boolean passivated;
			
			// if agent has new messages
			if(hasNewMessages)
			{
				// set flag to false and don't passivate
				updates.set("hasNewMessages", false);
				passivated = false;
			}
			else
			{
				// else do passivate
				updates.set("state", AgentState.PASSIVE)
						.set("activeInstances", 0);
				passivated = true;
			}
			
			UpdateResults res = ds.update(query, updates);
			
			// if updated return, else retry
			if(res.getUpdatedCount() > 0)
			{
				return passivated;
			}
			else
			{
				log.warn("Passivation of agent <{}> failed due to state change, try count {}, will retry at most {} times."
						+ " Expected: {}"
						,agentName, i+1, MAX_RETRIES, query);
			}
		}
		
		// if unsuccessful after MAX_RETRIES times
		log.error("Passivation of agent <{}> failed due to state change after {} retries, aborting.", agentName);
		throw new AgentStateChangeFailed("Unable to passivate agent.");
	}
	
	public Integer activateMultiThreadedInstance(String agentName)
	{
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.inc("activeInstances")
				.set("state", AgentState.ACTIVE);
		
		AgentEntity agent = ds.findAndModify(basicQuery(agentName), updates, false);
		
		return agent.getActiveInstances();
	}
	
	public Integer deactivateMultiThreadedInstance(String agentName)
	{
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.dec("activeInstances");
		
		AgentEntity agent = ds.findAndModify(basicQuery(agentName), updates, false);
		
		passivateMultiThreaded(agentName);
		
		return agent.getActiveInstances();
	}
	
	private void passivateMultiThreaded(String agentName)
	{
		Query<AgentEntity> query = basicQuery(agentName)
				.field("activeInstances").equal(0);
		
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.set("state", AgentState.PASSIVE);
		
		ds.update(query, updates);
	}
	
	public void forcePassivate(String agentName)
	{
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.set("state", AgentState.PASSIVE)
				.set("activeInstances", 0);
		
		ds.update(basicQuery(agentName), updates);
	}
	
	// messages
	
	public void putToInbox(String agentName, ACLMessage message)
	{
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.add("messages", message)
				.set("hasNewMessages", true);
		
		ds.update(basicQuery(agentName), updates);
	}
	
	public void putBackToInbox(String agentName, ACLMessage message)
	{
		// bind message to agent
		UpdateOperations<AgentEntity> agentUpdates = ds.createUpdateOperations(AgentEntity.class)
				.add("messages", message);
		
		ds.update(basicQuery(agentName), agentUpdates);
		
		saveMessageBackToDB(message);
	}
	
	private void saveMessageBackToDB(ACLMessage message)
	{
		// try at most MAX_RETRIES times
		for(int i=0; i<MAX_RETRIES; i++)
		{
			// increase unreadCount or insert
			if(ds.exists(message) != null)
			{
				Query<ACLMessage> query = ds.find(ACLMessage.class, "_id", message.getId());
				
				UpdateOperations<ACLMessage> updates = ds.createUpdateOperations(ACLMessage.class)
						.inc("unreadCount");
				
				UpdateResults res = ds.update(query, updates);
				
				// if updated return, else retry
				if(res.getUpdatedCount() > 0)
				{
					return;
				}
			}
			else
			{
				try
				{
					message.setUnreadCount(1);
					ds.insert(message);
					return;
				}
				catch(DuplicateKeyException e)
				{/*retry*/}
			}
		}
		
		// if unsuccessful after MAX_RETRIES times
		log.error("Putting a message back to inbox failed after {} attempts.", MAX_RETRIES);
		throw new AgentStateChangeFailed("Unable to put a message back to inbox.");
	}
	
	public List<ACLMessage> retrieveFromInbox(String agentName)
	{
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.set("messages", Collections.<ACLMessage>emptyList())
				.set("hasNewMessages", false);
		
		// clear messages, set hasNewMessages to false and return old AgentEntity version
		AgentEntity agent = ds.findAndModify(basicQuery(agentName), updates, true);
		assertAgentExists(agent, agentName);
		
		return new ArrayList<ACLMessage>(agent.getMessages());
	}
	
	public void removeFromInbox(String agentName, List<ACLMessage> messages)
	{
		if(messages.isEmpty()) return;
		
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.removeAll("messages", messages)
				.set("hasNewMessages", false);
		
		ds.update(basicQuery(agentName), updates);
	}
	
	public List<String> getMessageIDs(String agentName)
	{
		Query<AgentEntity> query = basicQuery(agentName).retrievedFields(true, "messages");
		
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.set("hasNewMessages", false);
		
		return ds.findAndModify(query, updates).getMessages()
				.stream()
				.map(m->m.getId())
				.collect(Collectors.toList());
	}
	
	public boolean hasNewMessages(String agentName)
	{
		return basicQuery(agentName)
				.retrievedFields(true, "hasNewMessages")
				.get()
				.hasNewMessages();
	}
	
	public void ignoreNewMessages(String agentName)
	{
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.set("hasNewMessages", false);
		
		ds.update(basicQuery(agentName), updates);
	}
	
	// data query
	
	public Integer getActiveInstances(String agentName)
	{
		return basicQuery(agentName)
				.retrievedFields(true, "activeInstances")
				.get()
				.getActiveInstances();
	}
	
	public boolean containsAgent(AgentIdentifier aid)
	{
		return containsAgent(aid.getName());
	}
	
	public boolean containsAgent(String agentName)
	{
		return basicQuery(agentName).countAll() > 0;
	}
	
	// transport
	
	public AgentEntity depart(String agentName)
	{
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.set("state", AgentState.IN_TRANSIT);
		
		return ds.findAndModify(basicQuery(agentName), updates, true);
	}
	
	public List<ACLMessage> completeDeparture(String agentName)
	{
		AgentEntity agent = ds.findAndDelete(basicQuery(agentName));
		
		return new ArrayList<ACLMessage>(agent.getMessages());
	}
	
	private AgentEntity find(String agentName)
	{
		return basicQuery(agentName).get();
	}

	private Query<AgentEntity> basicQuery(String agentName)
	{
		return ds.find(AgentEntity.class, "aid", new AgentIdentifier(agentName));
	}
	
	private void assertAgentExists(AgentEntity agent, String agentName)
	{
		if(agent == null)
		{
			throw new AgentNotFound("Agent not found: <"+agentName+">");
		}
	}
}
