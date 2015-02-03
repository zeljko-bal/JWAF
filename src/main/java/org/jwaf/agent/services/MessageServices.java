package org.jwaf.agent.services;

import java.util.List;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;

public class MessageServices
{
	private MessageSender messageSender;
	private AgentManager agentManager;
	private AgentIdentifier aid;
	
	public MessageServices(MessageSender messageSender, AgentManager agentManager)
	{
		this.messageSender = messageSender;
		this.agentManager = agentManager;
	}
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}
	
	public void send(ACLMessage message)
	{
		message.setSender(aid);
		messageSender.send(message);
	}
	
	public List<ACLMessage> getAll()
	{
		return agentManager.getMessages(aid.getName());
	}
	
	public boolean newMessagesAvailable()
	{
		return agentManager.findView(aid.getName()).hasNewMessages();
	}
	
	public void ignoreNewMessages()
	{
		agentManager.ignoreNewMessages(aid.getName());
	}
	
	public void ignoreAndForgetNewMessages()
	{
		getAll();
	}
}
