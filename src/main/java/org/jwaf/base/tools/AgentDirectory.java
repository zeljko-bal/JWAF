package org.jwaf.base.tools;

import java.util.List;
import java.util.Map;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.data.management.AgentDataManager;
import org.jwaf.data.persistence.entity.AgentDataType;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.remote.persistence.entity.AgentPlatform;

public class AgentDirectory
{
	private AidManager aidManager;
	private AgentManager agentManager;
	private AgentDataManager agentDataManager;
	private RemotePlatformManager remoteManager;
	private String localPlatformName;
	
	public AgentDirectory(AidManager aidManager, AgentManager agentManager, AgentDataManager agentDataManager, RemotePlatformManager remoteManager, String localPlatformName)
	{
		this.aidManager = aidManager;
		this.agentManager = agentManager;
		this.agentDataManager = agentDataManager;
		this.remoteManager = remoteManager;
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
		return remoteManager.containsAid(aid.getName(), platformName);
	}
	
	public boolean remotePlatformContains(String name, String platformName)
	{
		return remoteManager.containsAid(name, platformName);
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
			AgentPlatform location = remoteManager.locationOf(agentName);
			
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
		return agentDataManager.getDataStore(agentName, AgentDataType.PUBLIC);
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
