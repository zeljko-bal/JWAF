package org.jwaf.agent.persistence.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

/**
 * Session Bean implementation class AgentRepository
 */
@Stateless
@LocalBean
public class AgentRepository 
{
	private static final int MAX_RETRIES = 50;
	
	@MorphiaAdvancedDatastore
	private AdvancedDatastore ds;
	
	@Inject
	private Logger log;
	
	public AgentEntity findAgent(String name)
	{
		return find(name);
	}
	
	public AgentEntityView findView(String name)
	{
		AgentEntityView ret = find(name);
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
	
	public void remove(String name)
	{
		ds.delete(AgentEntity.class, name);
	}
	
	public String activate(String agentName, ACLMessage message)
	{
		// try at most MAX_RETRIES times
		for(int i=0; i<MAX_RETRIES; i++)
		{
			// retrieve previous state
			AgentEntity agent = basicQuery(agentName)
					.retrievedFields(true, "state")
					.get();
			
			if(agent == null)
			{
				throw new AgentNotFound();
			}
			
			String prevState = agent.getState();
			
			// if still INITIALIZING do nothing
			if(AgentState.INITIALIZING.equals(prevState))
			{
				return prevState;
			}
			
			Query<AgentEntity> query = basicQuery(agentName)
					.field("state").equal(prevState);
			
			UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
					.add("messages", message)
					.set("hasNewMessages", true);
			
			// if prevState was PASSIVE activate
			if(AgentState.PASSIVE.equals(prevState))
			{
				// activate agent
				updates.set("state", AgentState.ACTIVE);
			}
			
			UpdateResults res = ds.update(query, updates);
			
			// if updated return, else retry
			if(res.getUpdatedCount() > 0)
			{
				return prevState;
			}
			else
			{
				log.warn("Activation of agent <{}> failed due to state change, try count {}, will retry at most {} times."
						,agentName, i+1, MAX_RETRIES);
			}
		}
		
		// if unsuccessful after MAX_RETRIES times
		log.error("Activation of agent <{}> failed due to state change after {} retries, aborting.", agentName);
		throw new AgentStateChangeFailed("Unable to activate agent.");
	}
	
	public boolean passivate(String agentName, boolean force)
	{
		// try at most MAX_RETRIES times
		for(int i=0; i<MAX_RETRIES; i++)
		{
			// retrieve hasNewMessages flag
			AgentEntity agent = basicQuery(agentName)
					.retrievedFields(true, "hasNewMessages")
					.get();
			
			if(agent == null)
			{
				throw new AgentNotFound();
			}
			
			boolean hasNewMessages = agent.hasNewMessages();
			
			Query<AgentEntity> query = basicQuery(agentName)
					.field("hasNewMessages").equal(hasNewMessages);
			
			UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class);
			
			boolean passivated;
			
			// if agent has new messages and isn't forced to passivate
			if(agent.hasNewMessages() && !force)
			{
				// set flag to false and don't passivate
				updates.set("hasNewMessages", false);
				passivated = false;
			}
			else
			{
				// else do passivate
				updates.set("state", AgentState.PASSIVE);
				passivated = true;
			}
			
			UpdateResults res = ds.update(query, updates);
			
			// if updated return, else retry
			if(res.getUpdatedCount() > 0)
			{
				return passivated;
			}
		}
		
		// if unsuccessful after MAX_RETRIES times
		log.error("Passivation of agent <{}> failed due to state change after {} retries, aborting.", agentName);
		throw new AgentStateChangeFailed("Unable to passivate agent.");
	}
	
	public List<ACLMessage> retrieveMessages(String agentName)
	{
		UpdateOperations<AgentEntity> updates = ds.createUpdateOperations(AgentEntity.class)
				.set("messages", Collections.<ACLMessage>emptyList())
				.set("hasNewMessages", false);
		
		// clear messages, set hasNewMessages to false and return old AgentEntity version
		AgentEntity agent = ds.findAndModify(basicQuery(agentName), updates, true);
		
		if(agent == null)
		{
			throw new AgentNotFound();
		}
		
		return new ArrayList<ACLMessage>(agent.getMessages());
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
	
	public boolean containsAgent(AgentIdentifier aid)
	{
		return containsAgent(aid.getName());
	}
	
	public boolean containsAgent(String agentName)
	{
		return basicQuery(agentName).countAll() > 0;
	}
	
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
		return ds.find(AgentEntity.class, "aid", agentName);
	}
}
