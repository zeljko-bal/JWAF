package org.jwaf.base.tools;

import java.util.List;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.base.SerializableAgent;
import org.jwaf.remote.exceptions.AgentDeparted;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.remote.persistence.entity.AgentPlatform;

/**
 * A facade exposing functionalities of platform manager beans that deal with remote platforms.
 * 
 * @author zeljko.bal
 */
public class RemotePlatformTools
{
	private RemotePlatformManager remoteManager;
	private AgentIdentifier aid;
	private SerializableAgent owner;
	
	public RemotePlatformTools(SerializableAgent owner, RemotePlatformManager remoteManager)
	{
		this.owner = owner;
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
	
	public List<AgentIdentifier> getAgentIds(String platformName)
	{
		return remoteManager.getAgentIds(platformName);
	}
	
	public AgentPlatform locationOf(String agentName)
	{
		return remoteManager.locationOf(agentName);
	}
	
	public void travelTo(String platformName) throws AgentDeparted
	{
		remoteManager.sendAgent(aid.getName(), platformName, owner.serialize());
	}
}
