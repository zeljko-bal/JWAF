package org.jwaf.agent.services;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Stateless
@LocalBean
public class AgentDirectory
{
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private RemotePlatformServices remoteService;
	
	/*
	 * Find agent
	 */
	
	public AgentIdentifier find(String name)
	{
		// TODO find(String name) local or global
		return null;
	}
	
	public List<AgentIdentifier> find(Map<String, String> publicData)
	{
		// TODO find(Map<String, String> publicData) local or global
		return null;
	}
	
	/*
	 * Local platform
	 */
	
	public boolean localPlatformContains(AgentIdentifier aid)
	{
		return agentManager.contains(aid.getName());
	}
	
	public boolean localPlatformContains(String name)
	{
		return agentManager.contains(name);
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
