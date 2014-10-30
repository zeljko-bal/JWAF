package org.jwaf.agent.services;

import java.util.List;

import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.message.management.MessageManager;
import org.jwaf.message.persistence.entity.ACLMessage;

public class MessageServices
{	
	@Inject
	private MessageManager messageManager;
	
	@Inject
	private AgentRepository agentRepo;
	
	private AgentIdentifier aid;
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}
	
	public void sendMessage(ACLMessage message)
	{
		message.setSender(aid);
		messageManager.handleMessage(message);
	}
	
	public List<ACLMessage> getMessages()
	{
		return agentRepo.getMessages(aid.getName());
	}
	
	public boolean newMessagesAvailable()
	{
		return agentRepo.findView(aid.getName()).hasNewMessages();
	}
	
	public void ignoreNewMessages()
	{
		agentRepo.ignoreNewMessages(aid.getName());
	}
}
