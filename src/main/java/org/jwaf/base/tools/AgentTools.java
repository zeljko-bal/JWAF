package org.jwaf.base.tools;

import java.net.URL;

import org.jwaf.agent.exceptions.AgentSelfTerminatedException;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.data.management.AgentDataManager;
import org.jwaf.data.persistence.entity.AgentDataType;
import org.jwaf.data.persistence.repository.DataStore;

public class AgentTools
{
	private AgentManager agentManager;
	private AidManager aidManager;
	private AgentDataManager agentDataManager;
	private AgentIdentifier aid;
	
	public AgentTools(AgentManager agentManager, AidManager aidManager, AgentDataManager agentDataManager)
	{
		this.agentManager = agentManager;
		this.aidManager = aidManager;
		this.agentDataManager = agentDataManager;
	}

	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}
	
	/*
	 * data
	 */
	
	public DataStore getData(AgentDataType type)
	{
		return agentDataManager.getDataStore(aid.getName(), type);
	}
	
	public void addAddress(URL address)
	{
		aidManager.addAddress(aid.getName(), address);
	}
	
	public void removeAddress(URL address)
	{
		aidManager.removeAddress(aid.getName(), address);
	}
	
	public void addResolver(AgentIdentifier aid)
	{
		aidManager.addResolver(aid.getName(), aid);
	}
	
	public void removeResolver(AgentIdentifier aid)
	{
		aidManager.removeResolver(aid.getName(), aid);
	}
	
	/*
	 * admin
	 */
	
	public void terminate()
	{
		agentManager.remove(aid.getName());
		throw new AgentSelfTerminatedException("agent <" + aid.getName() + "> self terminated.");
	}
	
	/*
	 * state
	 */
	
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
