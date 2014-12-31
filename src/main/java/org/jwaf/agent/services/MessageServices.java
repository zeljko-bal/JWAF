package org.jwaf.agent.services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class MessageServices
{
	@Inject
	private MessageSender messageSender;
	
	@Inject
	private AgentManager agentManager;
	
	private AgentIdentifier aid;
	
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
