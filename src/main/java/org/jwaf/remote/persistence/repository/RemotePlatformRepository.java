package org.jwaf.remote.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.annotation.event.RemoteAidUnregisteredEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.persistence.entity.AgentPlatform;

@Stateless
@LocalBean
public class RemotePlatformRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@Inject @RemoteAidUnregisteredEvent
	private Event<AgentIdentifier> remoteAidUnregisteredEvent;
	
	public AgentPlatform find(String name)
	{
		return em.find(AgentPlatform.class, name);
	}
	
	private List<AgentIdentifier> getAidResultList(String name)
	{
		return em.createQuery("SELECT a FROM AgentIdentifier a WHERE a.name like :name AND a MEMBER OF SELECT pl.agentAids FROM AgentPlatform AS pl", AgentIdentifier.class).setParameter("name", name).getResultList();
	}
	
	public AgentIdentifier findAid(String name)
	{
		return getAidResultList(name).get(0);
	}
	
	public boolean contains(String name)
	{
		return find(name) != null;
	}
	
	public boolean containsAid(String name)
	{
		return !getAidResultList(name).isEmpty();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void register(AgentPlatform platform)
	{
		em.persist(platform);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void register(AgentIdentifier aid, String platformName)
	{
		AgentPlatform platform = em.find(AgentPlatform.class, platformName, LockModeType.PESSIMISTIC_WRITE);
		
		platform.getAgentIds().add(aid);
		
		em.merge(platform);
	}
	
	public List<AgentIdentifier> retrieveAgentIds(String platformName)
	{
		return find(platformName).getAgentIds();
	}
	
	public List<AgentPlatform> retrievePlatforms()
	{
		return em.createQuery("SELECT pl FROM AgentPlatform AS pl", AgentPlatform.class).getResultList();
	}
	
	
	public void unregister(String platformName)
	{
		AgentPlatform platform = em.find(AgentPlatform.class, platformName);
		
		unregisterTransactional(platform);
		
		platform.getAgentIds().forEach((AgentIdentifier aid) -> remoteAidUnregisteredEvent.fire(aid));
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void unregisterTransactional(AgentPlatform platform)
	{
		em.remove(platform);
	}
	
	public void unregister(String agentName, String platformName)
	{
		AgentIdentifier aid = unregisterAidTransactional(agentName, platformName);
		
		remoteAidUnregisteredEvent.fire(aid);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private AgentIdentifier unregisterAidTransactional(String agentName, String platformName)
	{
		AgentPlatform platform = em.find(AgentPlatform.class, platformName, LockModeType.PESSIMISTIC_WRITE);
		
		AgentIdentifier ret = null;
		
		for(AgentIdentifier aid : platform.getAgentIds())
		{
			if(aid.getName().equals(agentName)) 
			{
				ret = aid;
			}
		}
		
		platform.getAgentIds().removeIf((AgentIdentifier aid)-> aid.getName().equals(agentName));
		
		em.merge(platform);
		
		return ret;
	}
}
