package org.jwaf.agent.services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class MessageServices
{	
	@Inject
	private MessageSender messageSender;
	
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
		messageSender.send(message);
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
