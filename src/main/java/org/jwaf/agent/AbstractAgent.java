package org.jwaf.agent;

import java.util.List;

import javax.inject.Inject;

import org.jwaf.agent.annotation.AgentQualifier;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.DataStore;
import org.jwaf.agent.persistence.repository.DataStoreType;
import org.jwaf.message.persistence.entity.ACLMessage;

@AgentQualifier
public abstract class AbstractAgent
{
	protected AgentIdentifier aid;
	
	@Inject
	private AgentManager agentManager;
	
	public void setAid(AgentIdentifier aid) 
	{
		this.aid = aid;
	}

	public abstract void execute();
	
	public void setup()
	{/* no-op */}
	
	/*
	 * methods delegated to agentManager
	 */
	
	protected void sendMessage(ACLMessage message)
	{
		message.setSender(aid);
		agentManager.sendMessage(message);
	}
	
	protected List<ACLMessage> getMessages()
	{
		return agentManager.getMessages(aid);
	}
	
	protected boolean newMessagesAvailable()
	{
		return agentManager.hasNewMessages(aid);
	}
	
	protected void ignoreNewMessages()
	{
		agentManager.ignoreNewMessages(aid);
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
		return agentManager.getTypeOf(aid);
	}
	
	protected AgentType getType(AgentIdentifier aid)
	{
		return agentManager.getTypeOf(aid);
	}
	
	protected AgentType getType(String name)
	{
		return agentManager.getTypeOf(name);
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
}
