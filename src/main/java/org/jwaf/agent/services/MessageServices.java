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
	
	public MessageServices(MessageSender messageSender, AgentManager agentManager, AgentIdentifier aid)
	{
		this.messageSender = messageSender;
		this.agentManager = agentManager;
		this.aid = aid;
	}

	private AgentIdentifier aid;
	
	public void setAid(AgentIdentifier aid)
	{
		System.out.println("MessageServices "+toString()+" setting aid to "+aid.getName());
		this.aid = aid;
	}
	
	public void send(ACLMessage message)
	{
		System.out.println("message send aid: " + aid.getName());
		message.setSender(aid);
		System.out.println("sending message: " + message.getPerformative() + " from "+ message.getSender().getName());
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
