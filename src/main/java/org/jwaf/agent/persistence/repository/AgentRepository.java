package org.jwaf.agent.persistence.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentEntityView;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotations.LocalPlatformName;

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

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String activate(AgentIdentifier aid, ACLMessage message)
	{
		String prevState;

		AgentEntity agent = find(aid);

		em.lock(agent, LockModeType.PESSIMISTIC_WRITE);

		// get previous state
		prevState = agent.getState();
		
		// if agent is not yet initialized
		if(AgentState.INITIALIZING.equals(prevState))
		{
			// TODO throw not yet initialized
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
	public List<ACLMessage> getMessages(String name) 
	{
		AgentEntity agent = find(name);

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
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void ignoreNewMessages(String name)
	{
		AgentEntity agent = find(name);

		em.lock(agent, LockModeType.PESSIMISTIC_WRITE);
		
		agent.setHasNewMessages(false);
		
		em.merge(agent);
	}

	public DataStore getDataStore(String agentName, AgentDataType type)
	{
		return new DataStore(this, type, agentName);
	}
	
	public Map<String, String> getPublicData(String agentName)
	{
		AgentEntity agent = find(agentName);
		em.detach(agent);
		return agent.getData(AgentDataType.PUBLIC);
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

	public AgentIdentifier getPlatformAid()
	{
		return em.createQuery("SELECT a FROM AgentIdentifier a WHERE a.name LIKE :name", AgentIdentifier.class).setParameter("name", localPlatformName).getSingleResult();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void initializePlatformAid(String name, Map<String, String> parameters)
	{
		AgentIdentifier aid = new AgentIdentifier(name);
		if(parameters != null)
		{
			aid.getUserDefinedParameters().putAll(parameters);
		}
		
		em.persist(aid);
	}
}
