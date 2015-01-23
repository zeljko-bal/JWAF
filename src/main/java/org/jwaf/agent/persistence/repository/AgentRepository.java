package org.jwaf.agent.persistence.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentEntityView;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.persistence.entity.ACLMessage;

/**
 * Session Bean implementation class AgentRepository
 */
@Stateless
@LocalBean
public class AgentRepository 
{
	@PersistenceContext
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AgentEntity findAgent(String name)
	{
		return find(name);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AgentEntityView findView(String name)
	{
		// TODO maybe detatch
		return find(name);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void merge(AgentEntity agent)
	{
		em.merge(agent);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void create(AgentEntity agent)
	{
		em.persist(agent);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void remove(String name)
	{
		AgentEntity agent = find(name);
		
		em.remove(agent);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String activate(AgentIdentifier aid, ACLMessage message)
	{
		AgentEntity agent = em.find(AgentEntity.class, aid.getName(), LockModeType.PESSIMISTIC_WRITE);

		// get previous state
		String prevState = agent.getState();
		
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

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ACLMessage> getMessages(String name)
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
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Map<String, String> getPublicData(String agentName)
	{
		AgentEntity agent = find(agentName);
		em.detach(agent);
		return agent.getData(AgentDataType.PUBLIC);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean containsAgent(AgentIdentifier aid)
	{
		return contains(aid.getName());
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean containsAgent(String name)
	{
		return contains(name);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AgentEntity depart(String agentName)
	{
		AgentEntity agent = em.find(AgentEntity.class, agentName, LockModeType.PESSIMISTIC_WRITE);
		agent.setState(AgentState.IN_TRANSIT);
		em.merge(agent);
		return agent;
	}
	
	private AgentEntity find(String name)
	{
		return em.find(AgentEntity.class, name);
	}
	
	private boolean contains(String name)
	{
		return find(name) != null;
	}
}
