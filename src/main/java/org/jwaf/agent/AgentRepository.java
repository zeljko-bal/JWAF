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
		return find(aid.getName());
	}
	
	public AgentEntity find(String name)
	{
		return em.createQuery("SELECT a FROM AgentEntity a WHERE a.aid.name LIKE :name", AgentEntity.class).setParameter("name", name).getResultList().get(0);
	}
	
	public void merge(AgentEntity agent)
	{
		em.merge(agent);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String activate(AgentIdentifier aid, ACLMessage message)
	{
		String prevState;
		
		AgentEntity agent = find(aid);

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
	
	public boolean passivate(AgentIdentifier aid)
	{
		return passivate(aid, false);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean passivate(AgentIdentifier aid, boolean force)
	{
		AgentEntity agent = find(aid);
		
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
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ACLMessage> getMessages(AgentIdentifier aid) 
	{
		AgentEntity agent = find(aid);
		
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
		return contains(aid.getName());
	}
	
	public boolean contains(String name)
	{
		return !(em.createQuery("SELECT a FROM AgentEntity a WHERE a.aid.name LIKE :name").setParameter("name", name).getResultList().isEmpty());
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
			List<AgentIdentifier> results = em.createQuery("SELECT a FROM AgentIdentifier a WHERE a.name like :name", AgentIdentifier.class)
					.setParameter("name", aid.getName())
					.getResultList();
			if(!results.isEmpty())
			{
				return results.get(0);
			}
		}
		else
		{
			// TODO throw or log, name cant be null
			return null;
		}

		// else persist aid
		em.persist(aid);
		em.flush();
		return aid;
	}
}
