package org.jwaf.agent.persistence.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentEntityView;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.annotation.event.MessageRemovedEvent;
import org.jwaf.message.annotation.event.MessageRetrievedEvent;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotation.resource.LocalPlatformName;

/**
 * Session Bean implementation class AgentRepository
 */
@Stateless
@LocalBean
public class AgentRepository 
{
	@PersistenceContext
	private EntityManager em;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @MessageRetrievedEvent
	private Event<ACLMessage> messageRetrievedEvent;

	protected AgentEntity findAgent(String name)
	{
		return em.find(AgentEntity.class, name);
	}

	protected AgentEntity findAgent(AgentIdentifier aid)
	{
		return findAgent(aid.getName());
	}

	public AgentEntityView findView(String name)
	{
		// TODO maybe detatch
		return findAgent(name);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void merge(AgentEntity agent)
	{
		em.merge(agent);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void refresh(AgentEntity agent)
	{
		em.refresh(agent);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void create(AgentEntity agent)
	{
		em.persist(agent);
	}
	
	public void remove(String name)
	{
		AgentEntity agent = findAgent(name);
		
		removeTransactional(agent);
		
		removeOrphanedAid(agent.getAid());
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void removeTransactional(AgentEntity agent)
	{		
		em.remove(agent);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String activate(AgentIdentifier aid, ACLMessage message)
	{
		String prevState;

		AgentEntity agent = em.find(AgentEntity.class, aid.getName(), LockModeType.PESSIMISTIC_WRITE);

		// get previous state
		prevState = agent.getState();
		
		// if agent is not yet initialized
		if(AgentState.INITIALIZING.equals(prevState))
		{
			return prevState;
		}

		// add the message
		agent.getMessages().add(message);

		// set unread messages flag
		agent.setHasNewMessages(true);

		// activate agent
		agent.setState(AgentState.ACTIVE);

		// persist
		em.merge(agent);

		return prevState;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean passivate(AgentIdentifier aid, boolean force)
	{
		AgentEntity agent = em.find(AgentEntity.class, aid.getName(), LockModeType.PESSIMISTIC_WRITE);

		// if agent has new messages and isnt forced to passivate
		if(agent.hasNewMessages() && !force)
		{
			// set flag to false and dont passivate
			agent.setHasNewMessages(false);

			em.merge(agent);

			// not passivated
			return false;
		}
		else
		{
			// else do passivate
			agent.setState(AgentState.PASSIVE);

			em.merge(agent);

			// passivated
			return true;
		}
	}

	public List<ACLMessage> getMessages(String name) 
	{
		List<ACLMessage> messages = getMessagesTransactional(name);
		
		// notify that messages have ben retrieved
		messages.forEach((ACLMessage message) -> messageRetrievedEvent.fire(message));

		return messages;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private List<ACLMessage> getMessagesTransactional(String name)
	{
		AgentEntity agent = em.find(AgentEntity.class, name, LockModeType.PESSIMISTIC_WRITE);

		// get all messages
		List<ACLMessage> messages = new ArrayList<>(agent.getMessages());		

		// clear dependecies to message entities
		agent.getMessages().clear();

		// no more messages for now
		agent.setHasNewMessages(false);

		// commit changes
		em.merge(agent);

		return messages;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void ignoreNewMessages(String name)
	{
		AgentEntity agent = em.find(AgentEntity.class, name, LockModeType.PESSIMISTIC_WRITE);
		
		agent.setHasNewMessages(false);
		
		em.merge(agent);
	}

	public DataStore getDataStore(String agentName, AgentDataType type)
	{
		return new DataStore(this, type, agentName);
	}
	
	public Map<String, String> getPublicData(String agentName)
	{
		AgentEntity agent = findAgent(agentName);
		em.detach(agent);
		return agent.getData(AgentDataType.PUBLIC);
	}

	public boolean contains(AgentIdentifier aid)
	{
		return contains(aid.getName());
	}

	public boolean contains(String name)
	{
		return findAgent(name) != null;
	}

	public AgentIdentifier manageAID(AgentIdentifier aid)
	{
		// if aid is null
		if(aid == null)
		{
			// nothing to persist
			return null;
		}

		if(aid.getName() != null)
		{
			// if aid with same name is already persistant return
			if(!containsAid(aid.getName()))
			{
				return findAid(aid.getName());
			}
		}
		else
		{
			throw new NullPointerException("[AgentRepository#manageAID] Agent name cannot be null.");
		}

		// manage resolvers recursively
		aid.getResolvers().replaceAll((AgentIdentifier res) -> manageAID(res));

		// else persist aid
		em.persist(aid);
		em.flush();
		return aid;
	}
	
	protected AgentIdentifier findAid(String name)
	{
		return em.find(AgentIdentifier.class, name);
	}
	
	protected boolean containsAid(String name)
	{
		return findAid(name) != null;
	}

	public AgentIdentifier getPlatformAid()
	{
		return em.find(AgentIdentifier.class, localPlatformName);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AgentIdentifier createAid(String name, Map<String, String> parameters)
	{
		AgentIdentifier aid = new AgentIdentifier(name);
		if(parameters != null)
		{
			aid.getUserDefinedParameters().putAll(parameters);
		}
		
		em.persist(aid);
		
		return aid;
	}
	
	public void messageRemovedEventHandler(@Observes @MessageRemovedEvent ACLMessage message)
	{
		removeOrphanedAid(message.getSender());
		message.getReceiverList().forEach((AgentIdentifier aid) -> removeOrphanedAid(aid));
		message.getIn_reply_toList().forEach((AgentIdentifier aid) -> removeOrphanedAid(aid));
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void removeOrphanedAid(AgentIdentifier aid)
	{
		// TODO check dependencies, remove aid
	}
}
