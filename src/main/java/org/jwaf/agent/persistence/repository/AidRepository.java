package org.jwaf.agent.persistence.repository;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.annotation.event.AidReferenceDroppedEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Stateless
@LocalBean
public class AidRepository
{
	@PersistenceContext
	private EntityManager em;
	
	// TODO manageAID fix
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
				return aquireAidReference(aid.getName());
			}
		}
		else
		{
			throw new NullPointerException("[AgentRepository#manageAID] Agent name cannot be null.");
		}

		// manage resolvers recursively
		aid.getResolvers().replaceAll((AgentIdentifier res) -> manageAID(res));
		aid.setRefCount(1);

		// else persist aid
		em.persist(aid);
		em.flush();
		return aid;
	}
	
	private AgentIdentifier aquireAidReference(String name)
	{
		AgentIdentifier aid = em.find(AgentIdentifier.class, name, LockModeType.PESSIMISTIC_WRITE);
		if(aid != null)
		{
			aid.setRefCount(aid.getRefCount() + 1);
		}
		
		return aid;
	}
	
	public AgentIdentifier findAid(String name)
	{
		return em.find(AgentIdentifier.class, name);
	}
	
	public boolean containsAid(String name)
	{
		return findAid(name) != null;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AgentIdentifier createAid(AgentIdentifier aid)
	{
		aid.setRefCount(1);
		
		em.persist(aid);
		
		return aid;
	}
	
	public void aidReferenceDroppedEventHandler(@Observes @AidReferenceDroppedEvent String agentName)
	{
		AgentIdentifier aid = findAid(agentName);
		decrementAidReferenceCount(aid);
		removeOrphanedAid(aid);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void decrementAidReferenceCount(AgentIdentifier aid)
	{
		em.lock(aid, LockModeType.PESSIMISTIC_WRITE);
		aid.setRefCount(aid.getRefCount() - 1);
		em.merge(aid);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private	void removeOrphanedAid(AgentIdentifier aid)
	{
		em.lock(aid, LockModeType.PESSIMISTIC_WRITE);
		if(aid.getRefCount() <= 0)
		{
			em.remove(aid);
		}
	}
}
