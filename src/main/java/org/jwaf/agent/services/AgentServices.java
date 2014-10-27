package org.jwaf.agent.services;

import java.util.Map;

import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.CreateAgentRequest;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentDataType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.agent.persistence.repository.DataStore;

public class AgentServices
{
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private AgentRepository agentRepo;
	
	private AgentIdentifier aid;
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}
	
	/*
	 * local platform aids
	 */
	
	public boolean localPlatformContains(AgentIdentifier aid)
	{
		return agentManager.contains(aid);
	}
	
	public boolean localPlatformContains(String name)
	{
		return agentManager.contains(name);
	}
	
	/*
	 * data
	 */
	
	public DataStore getData(AgentDataType type)
	{
		return agentRepo.getDataStore(aid.getName(), type);
	}
	
	public Map<String, String> getPublicData(String agentName)
	{
		return agentManager.getPublicData(agentName);
	}
	
	/*
	 * admin
	 */
	
	public AgentIdentifier createAgent(CreateAgentRequest request)
	{
		return agentManager.createAgent(request);
	}
	
	public void deleteAgent(String name)
	{
		agentManager.deleteAgent(name);
	}
	
	/*
	 * state
	 */
	
	public String getState()
	{
		return agentManager.find(aid).getState();
	}
	
	public String getState(AgentIdentifier aid)
	{
		return agentManager.find(aid).getState();
	}
	
	public String getState(String name)
	{
		return agentManager.find(name).getState();
	}
	
	/*
	 * type
	 */
	
	protected AgentType getType()
	{
		return agentManager.getTypeOf(aid.getName());
	}
	
	protected AgentType getType(AgentIdentifier aid)
	{
		return agentManager.getTypeOf(aid);
	}
	
	protected AgentType getTypeOf(String name)
	{
		return agentManager.getTypeOf(name);
	}
}
