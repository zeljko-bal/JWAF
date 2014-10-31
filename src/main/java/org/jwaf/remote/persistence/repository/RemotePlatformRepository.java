package org.jwaf.remote.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.persistence.entity.AgentPlatform;

@Stateless
@LocalBean
public class RemotePlatformRepository
{
	@PersistenceContext
	private EntityManager em;
	
	// TODO RemotePlatformRepository
	
	public AgentPlatform find(String name)
	{
		return null;
		
	}
	
	public AgentIdentifier findAid(String name)
	{
		return null;
		
	}
	
	public boolean contains(String name)
	{
		return false;
		
	}
	
	public boolean containsAid(String name)
	{
		return false;
		
	}
	
	public void register(AgentPlatform platform)
	{
		
	}
	
	public void register(AgentIdentifier aid, String platformName)
	{
		
	}
	
	public List<AgentIdentifier> retrieveAids(String platformName)
	{
		return null;
		
	}
	
	public List<AgentPlatform> retrievePlatforms()
	{
		return null;
		
	}

	public void unregister(String platformName)
	{
		// TODO Auto-generated method stub
	}

	public void unregister(String agentName, String platformName)
	{
		// TODO Auto-generated method stub
	}
}
