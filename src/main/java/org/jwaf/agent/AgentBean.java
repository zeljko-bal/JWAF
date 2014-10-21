package org.jwaf.agent;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.agent.entity.AgentType;
import org.jwaf.agent.persistence.DataStore;
import org.jwaf.agent.persistence.DataStoreType;
import org.jwaf.message.entity.ACLMessage;

@Stateless
@LocalBean
public abstract class AgentBean
{
	protected AgentIdentifier aid;
	
	@Inject
	private AgentManager agentManager;
	
	public void setAid(AgentIdentifier aid) 
	{
		this.aid = aid;
	}

	public abstract void execute();
	
	/*
	 * methods delegated to agentManager
	 */
	
	protected List<ACLMessage> getMessages()
	{
		return agentManager.getMessages(aid);
	}

	protected boolean localPlatformContains(AgentIdentifier aid)
	{
		return agentManager.contains(aid);
	}
	
	protected boolean localPlatformContains(String name)
	{
		return agentManager.contains(name);
	}
	
	protected AgentType getType()
	{
		return agentManager.getType(aid);
	}
	
	protected AgentType getType(AgentIdentifier aid)
	{
		return agentManager.getType(aid);
	}
	
	protected AgentType getType(String name)
	{
		return agentManager.getType(name);
	}
	
	protected DataStore getData(DataStoreType type)
	{
		return agentManager.getDataStore(aid.getName(), type);
	}
	
	protected String getState()
	{
		return agentManager.getState(aid);
	}
	
	protected String getState(AgentIdentifier aid)
	{
		return agentManager.getState(aid);
	}
	
	protected String getState(String name)
	{
		return agentManager.getState(name);
	}
	
	protected boolean newMessagesAvailable()
	{
		return agentManager.hasNewMessages(aid);
	}
}
