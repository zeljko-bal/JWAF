package org.jwaf.agent.persistence.repository;

import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.annotation.LocalPlatformAid;
import org.jwaf.agent.annotation.event.AidReferenceDroppedEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.platform.annotation.resource.LocalPlatformName;

@Stateless
@LocalBean
public class AidRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
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
	
	public AgentIdentifier findAid(String name)
	{
		return em.find(AgentIdentifier.class, name);
	}
	
	public boolean containsAid(String name)
	{
		return findAid(name) != null;
	}

	@Produces @LocalPlatformAid
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
	
	public void aidReferenceDroppedEventHandler(@Observes @AidReferenceDroppedEvent AgentIdentifier aid)
	{
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
