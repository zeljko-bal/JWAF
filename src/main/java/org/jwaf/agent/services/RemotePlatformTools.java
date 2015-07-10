package org.jwaf.agent.services;

import java.util.List;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.exceptions.AgentTransportFailed;
import org.jwaf.remote.exceptions.AgentTransportSuccessful;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.remote.persistence.entity.AgentPlatform;

public class RemotePlatformTools
{
	private RemotePlatformManager remoteManager;
	private AgentIdentifier aid;
	
	public RemotePlatformTools(RemotePlatformManager remoteManager)
	{
		this.remoteManager = remoteManager;
	}
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}

	public AgentPlatform find(String name)
	{
		return remoteManager.findPlatform(name);
	}
	
	public AgentIdentifier findAid(String name)
	{
		return remoteManager.findAid(name);
	}
	
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
	
	public void travelTo(String platformName) throws AgentTransportSuccessful, AgentTransportFailed
	{
		remoteManager.sendAgent(aid.getName(), platformName);
	}
}
