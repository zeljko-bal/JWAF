package org.jwaf.remote.management;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.persistence.entity.AgentPlatform;
import org.jwaf.remote.persistence.repository.RemotePlatformRepository;

@Stateless
@LocalBean
public class RemotePlatformManager
{
	@Inject
	RemotePlatformRepository repo;
	
	// TODO RemotePlatformManager REST
	
	public AgentPlatform find(String name)
	{
		return repo.find(name);
	}
	
	public AgentIdentifier findAid(String name)
	{
		return repo.findAid(name);
	}
	
	public boolean contains(String name)
	{
		return repo.contains(name);
	}
	
	public boolean containsAid(String name)
	{
		return repo.containsAid(name);
	}
	
	public void register(AgentPlatform platform)
	{
		repo.register(platform);
	}
	
	public void register(AgentIdentifier aid, String platformName)
	{
		repo.register(aid, platformName);
	}
	
	public List<AgentIdentifier> retrieveAids(String platformName)
	{
		return repo.retrieveAids(platformName);
	}
	
	public List<AgentPlatform> retrievePlatforms()
	{
		return repo.retrievePlatforms();
	}
	
	// TODO agentCreatedEventHandler
	public void agentCreatedEventHandler()
	{
		// notify others
	}
	
	// TODO agentRemovedEventHandler
	public void agentRemovedEventHandler()
	{
		// notify others
	}
}
