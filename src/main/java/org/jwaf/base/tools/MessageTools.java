package org.jwaf.base.tools;

import java.util.List;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;

/**
 *  A facade exposing functionalities of platform manager beans that deal with sending and receiving messages.
 * 
 * @author zeljko.bal
 */
public class MessageTools
{
	private MessageSender messageSender;
	private AgentManager agentManager;
	private AgentIdentifier aid;
	
	public MessageTools(MessageSender messageSender, AgentManager agentManager)
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
	
	public void reply(ACLMessage to, ACLMessage message)
	{
		message.addReceivers(to.getSender());
		send(message);
	}
	
	public List<ACLMessage> getAll()
	{
		return agentManager.retrieveMessages(aid.getName());
	}
	
	public List<ACLMessage> find(QueryFunction<ACLMessage> queryFunc)
	{
		return agentManager.findMessages(aid.getName(), queryFunc);
	}
	
	public int getCount()
	{
		return agentManager.getMessagesCount(aid.getName());
	}
	
	public boolean newMessagesAvailable()
	{
		return agentManager.hasNewMessages(aid.getName());
	}
	
	public void ignoreNewMessages()
	{
		agentManager.ignoreNewMessages(aid.getName());
	}
	
	public void ignoreAndForgetNewMessages()
	{
		getAll();
	}

	public void putBackToInbox(ACLMessage aclMessage)
	{
		agentManager.putBackToInbox(aid.getName(), aclMessage);
	}
}
