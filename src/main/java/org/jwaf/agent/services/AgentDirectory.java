package org.jwaf.agent.services;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.persistence.entity.AgentPlatform;

@Stateless
@LocalBean
public class AgentDirectory
{
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private AidManager aidManager;
	
	@Inject
	private RemotePlatformServices remoteService;
	
	/*
	 * Find agent
	 */
	
	public AgentIdentifier findAid(String name)
	{
		return aidManager.find(name);
	}
	
	public List<AgentIdentifier> findAid(Map<String, String> publicData)
	{
		return aidManager.find(publicData);
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
	
	public AgentPlatform locationOf(AgentIdentifier aid)
	{
		return remoteService.locationOf(aid.getName());
	}
	
	public AgentPlatform locationOf(String agentName)
	{
		return remoteService.locationOf(agentName);
	}
	
	/*
	 * Data
	 */
	
	public Map<String, String> getPublicData(String agentName)
	{
		return agentManager.getPublicData(agentName);
	}
	
	/*
	 * State
	 */
	
	public String getState(AgentIdentifier aid)
	{
		return agentManager.findView(aid.getName()).getState();
	}
	
	public String getState(String name)
	{
		return agentManager.findView(name).getState();
	}
}
