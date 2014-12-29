package org.jwaf.agent.services;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.exception.AgentSelfTerminatedException;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.agent.persistence.repository.AgentDataType;
import org.jwaf.agent.persistence.repository.DataStore;

@Stateless
@LocalBean
public class AgentServices
{
	@Inject
	private AgentManager agentManager;
	
	private AgentIdentifier aid;
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}
	
	/*
	 * data
	 */
	
	public DataStore getData(AgentDataType type)
	{
		return agentManager.getDataStore(aid.getName(), type);
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
		agentManager.remove(aid.getName());
		throw new AgentSelfTerminatedException("agent <" + aid.getName() + "> self terminated.");
	}
	
	/*
	 * state
	 */
	
	// TODO remove if unused/unnecessery
	public String getState()
	{
		return agentManager.findView(aid.getName()).getState();
	}
	
	/*
	 * type
	 */
	
	public AgentType getType()
	{
		return agentManager.findView(aid.getName()).getType();
	}
}
