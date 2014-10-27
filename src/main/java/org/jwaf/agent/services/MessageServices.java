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
		List<ACLMessage> messages = agentRepo.getMessages(aid.getName());

		// notify that messages have ben retrieved
		// TODO fireMessageRetrievedEvent
		messages.forEach((ACLMessage message)->{/*fireMessageRetrievedEvent(message);*/});

		return messages;
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
