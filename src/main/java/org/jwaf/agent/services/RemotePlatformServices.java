package org.jwaf.agent.services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.remote.persistence.entity.AgentPlatform;

@Stateless
@LocalBean
public class RemotePlatformServices
{
	@Inject
	private RemotePlatformManager remoteManager;
	
	public boolean containsPlatform(String name)
	{
		return remoteManager.containsPlatform(name);
	}
	
	public boolean containsAid(String name, String platformName)
	{
		return remoteManager.containsAid(name, platformName);
	}
	
	public List<AgentPlatform> retrievePlatforms()
	{
		return remoteManager.retrievePlatforms();
	}
	
	public List<AgentIdentifier> retrieveAgentIds(String platformName)
	{
		return remoteManager.retrieveAgentIds(platformName);
	}
	
	public AgentPlatform locationOf(String agentName)
	{
		return remoteManager.locationOf(agentName);
	}
}
