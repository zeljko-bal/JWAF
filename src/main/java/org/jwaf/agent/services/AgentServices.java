package org.jwaf.agent.services;

import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.exception.AgentSelfTerminatedException;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.management.CreateAgentRequest;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentDataType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.agent.persistence.repository.DataStore;

@Stateless
@LocalBean
public class AgentServices
{
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private AgentRepository agentRepo;
	
	@Inject
	private AgentTypeManager typeManager;
	
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
		return agentRepo.contains(aid.getName());
	}
	
	public boolean localPlatformContains(String name)
	{
		return agentRepo.contains(name);
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
		return agentRepo.getPublicData(agentName);
	}
	
	/*
	 * admin
	 */
	
	public AgentIdentifier createAgent(CreateAgentRequest request)
	{
		return agentManager.initialize(request);
	}
	
	public void requestAgentTermination(String name)
	{
		agentManager.requestTermination(name);
	}
	
	public void terminateSelf()
	{
		agentRepo.remove(aid.getName());
		throw new AgentSelfTerminatedException("agent <" + aid.getName() + "> self terminated.");
	}
	
	/*
	 * state
	 */
	
	public String getState()
	{
		return agentRepo.findView(aid.getName()).getState();
	}
	
	public String getState(AgentIdentifier aid)
	{
		return agentRepo.findView(aid.getName()).getState();
	}
	
	public String getState(String name)
	{
		return agentRepo.findView(name).getState();
	}
	
	/*
	 * type
	 */
	
	public AgentType getType()
	{
		return agentRepo.findView(aid.getName()).getType();
	}
	
	public AgentType getTypeOf(AgentIdentifier aid)
	{
		return agentRepo.findView(aid.getName()).getType();
	}
	
	public AgentType getTypeOf(String name)
	{
		return agentRepo.findView(name).getType();
	}
	
	public AgentType findType(String typeName)
	{
		return typeManager.find(typeName);
	}
}
