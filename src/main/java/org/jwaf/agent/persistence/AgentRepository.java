package org.jwaf.agent.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.AgentEntityView;
import org.jwaf.agent.AgentState;
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
	
	protected AgentEntity find(String name)
	{
		List<AgentEntity> result = getResultList(name);
		
		if(!result.isEmpty())
		{
			return result.get(0);
		}
		else
		{
			return null;
		}
	}
	
	protected AgentEntity find(AgentIdentifier aid)
	{
		return find(aid.getName());
	}
	
	public AgentEntityView findView(String name)
	{
		// TODO maybe detatch
		return find(name);
	}
	
	public AgentEntityView findView(AgentIdentifier aid)
	{
		return findView(aid.getName());
	}
	
	public void merge(AgentEntity agent)
	{
		em.merge(agent);
	}
	
	public void create(AgentEntity agent)
	{
		em.persist(agent);
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
		
		// no more messages for now
		agent.setHasNewMessages(false);

		// commit changes
		em.merge(agent);

		return messages;
	}
	
	public DataStore getDataStore(String agentName, DataStoreType type)
	{
		return new DataStore(this, type, agentName);
	}

	public boolean contains(AgentIdentifier aid)
	{
		return contains(aid.getName());
	}
	
	public boolean contains(String name)
	{
		return !getResultList(name).isEmpty();
	}
	
	private List<AgentEntity> getResultList(String name)
	{
		return em.createQuery("SELECT a FROM AgentEntity a WHERE a.aid.name LIKE :name", AgentEntity.class).setParameter("name", name).getResultList();
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
		
		// manage resolvers recursively
		aid.getResolvers().replaceAll((AgentIdentifier res) -> manageAID(res));

		// else persist aid
		em.persist(aid);
		em.flush();
		return aid;
	}
}
