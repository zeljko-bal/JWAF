package org.jwaf.agent.tools;

import java.util.List;
import java.util.Map;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.remote.persistence.entity.AgentPlatform;

public class AgentDirectory
{
	private AidManager aidManager;
	private AgentManager agentManager;
	private RemotePlatformTools remoteService;
	private String localPlatformName;
	
	public AgentDirectory(AidManager aidManager, AgentManager agentManager, RemotePlatformTools remoteService, String localPlatformName)
	{
		this.aidManager = aidManager;
		this.agentManager = agentManager;
		this.remoteService = remoteService;
		this.localPlatformName = localPlatformName;
	}

	/*
	 * Find agent
	 */
	
	public AgentIdentifier findAid(String name)
	{
		return aidManager.find(name);
	}
	
	public List<AgentIdentifier> findAid(Map<String, String> userDefinedParameters)
	{
		return aidManager.find(userDefinedParameters);
	}
	
	public boolean localPlatformContains(AgentIdentifier aid)
	{
		return agentManager.contains(aid.getName());
	}
	
	public boolean localPlatformContains(String name)
	{
		return agentManager.contains(name);
	}
	
	public boolean remotePlatformContains(AgentIdentifier aid, String platformName)
	{
		return remoteService.containsAid(aid.getName(), platformName);
	}
	
	public boolean remotePlatformContains(String name, String platformName)
	{
		return remoteService.containsAid(name, platformName);
	}
	
	public String locationOf(AgentIdentifier aid)
	{
		return locationOf(aid.getName());
	}
	
	public String locationOf(String agentName)
	{
		if(localPlatformContains(agentName))
		{
			return localPlatformName;
		}
		else
		{
			AgentPlatform location = remoteService.locationOf(agentName);
			
			if(location != null)
			{
				return location.getName();
			}
			else
			{
				return null;
			}
		}
	}
	
	/*
	 * Data
	 */
	
	public Map<String, String> getPublicData(String agentName)
	{
		return agentManager.getPublicData(agentName);
	}
	
	/*
	 * StateCallback
	 */
	
	public String getState(AgentIdentifier aid)
	{
		return agentManager.findView(aid.getName()).getState();
	}
	
	public String getState(String name)
	{
		return agentManager.findView(name).getState();
	}
	
	/*
	 * Admin
	 */
	
	public AgentIdentifier createAgent(CreateAgentRequest request)
	{
		return agentManager.initialize(request);
	}
	
	public void requestAgentTermination(String name)
	{
		agentManager.requestTermination(name);
	}
}
