package org.jwaf.remote.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.persistence.entity.AgentPlatform;

@Stateless
@LocalBean
public class RemotePlatformRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private AidManager aidManager;
	
	public AgentPlatform findPlatform(String name)
	{
		return em.find(AgentPlatform.class, name);
	}
	
	private List<AgentIdentifier> getAidResultList(String name)
	{
		return em.createQuery(
				"SELECT a FROM AgentIdentifier a WHERE a.name = :name JOIN AgentPlatform pl WHERE a MEMBER OF pl.agentAids", 
				AgentIdentifier.class).setParameter("name", name).getResultList();
	}
	
	private List<AgentIdentifier> getAidResultList(String name, String platformName)
	{
		return em.createQuery(
				"SELECT a FROM AgentIdentifier a WHERE a.name = :name JOIN AgentPlatform pl WHERE a MEMBER OF pl.agentAids AND pl.name = :platformName", 
				AgentIdentifier.class).setParameter("name", name).setParameter("platformName", platformName).getResultList();
	}
	
	public AgentIdentifier findAid(String name)
	{
		return getAidResultList(name).get(0);
	}
	
	public boolean containsPlatform(String name)
	{
		return findPlatform(name) != null;
	}
	
	public boolean containsAid(String name)
	{
		return !getAidResultList(name).isEmpty();
	}
	
	public boolean containsAid(String name, String platformName)
	{
		return !getAidResultList(name, platformName).isEmpty();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void registerPlatform(AgentPlatform platform)
	{
		em.persist(platform);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void register(AgentIdentifier aid, String platformName)
	{
		AgentPlatform platform = em.find(AgentPlatform.class, platformName, LockModeType.PESSIMISTIC_WRITE);
		
		platform.getAgentIds().add(aidManager.manageAID(aid));
		
		em.merge(platform);
	}
	
	public List<AgentIdentifier> retrieveAgentIds(String platformName)
	{
		return findPlatform(platformName).getAgentIds();
	}
	
	public List<AgentPlatform> retrievePlatforms()
	{
		return em.createQuery("SELECT pl FROM AgentPlatform AS pl", AgentPlatform.class).getResultList();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unregisterPlatform(String platformName)
	{
		AgentPlatform platform = em.find(AgentPlatform.class, platformName, LockModeType.PESSIMISTIC_WRITE);
		
		em.remove(platform);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unregister(String agentName, String platformName)
	{
		AgentPlatform platform = em.find(AgentPlatform.class, platformName, LockModeType.PESSIMISTIC_WRITE);
		
		platform.getAgentIds().removeIf((AgentIdentifier aid)-> aid.getName().equals(agentName));
		
		em.merge(platform);
	}

	public AgentPlatform locationOf(String agentName)
	{
		List<AgentPlatform> result = em.createQuery("SELECT p FROM AgentPlatform p JOIN p.agentIds a WHERE :agentName = a.name", AgentPlatform.class).setParameter("agentName", agentName).getResultList();
		if(result.isEmpty())
		{
			return null;
		}
		else
		{
			return result.get(0);
		}
	}
}
