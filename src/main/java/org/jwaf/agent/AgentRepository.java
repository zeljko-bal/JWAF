package org.jwaf.agent;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.entity.AgentEntity;
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.message.entity.ACLMessage;

/**
 * Session Bean implementation class AgentRepository
 */
@Stateless
@LocalBean
public class AgentRepository 
{
	@PersistenceContext
	private EntityManager em;
	
	public void create(AgentEntity agent)
	{
		em.persist(agent);
	}
	
	public AgentEntity find(AgentIdentifier aid)
	{
		// TODO querry by aid
		return em.find(AgentEntity.class, 2);
	}
	
	public void merge(AgentEntity agent)
	{
		em.merge(agent);
		em.flush();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String activate(AgentEntity agent, ACLMessage message)
	{
		String prevState;

		em.lock(agent, LockModeType.PESSIMISTIC_WRITE);

		// get previous state
		prevState = agent.getState();

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
	
	public boolean passivate(AgentEntity agent)
	{
		return passivate(agent, false);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean passivate(AgentEntity agent, boolean force)
	{
		em.lock(agent, LockModeType.PESSIMISTIC_WRITE);

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

	public List<ACLMessage> getMessages(AgentIdentifier aid)
	{
		return getMessages(find(aid));
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ACLMessage> getMessages(AgentEntity agent) 
	{
		em.lock(agent, LockModeType.PESSIMISTIC_WRITE);

		// get all messages
		List<ACLMessage> messages = new ArrayList<>(agent.getMessages());		

		// clear dependecies to message entities
		agent.getMessages().clear();

		// commit changes
		em.merge(agent);

		return messages;
	}

	public boolean contains(AgentIdentifier aid)
	{
		// TODO jpql select a From AgentEntity a where a.aid like :aid
		return !(em.createQuery("").getResultList().isEmpty());
	}
}
