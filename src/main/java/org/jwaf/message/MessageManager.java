package org.jwaf.message;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jwaf.agent.AgentManager;
import org.jwaf.agent.entity.AgentEntity;
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.agent.entity.AgentType;
import org.jwaf.message.entity.ACLMessage;
import org.jwaf.message.entity.MessageEnvelope;

/**
 * Session Bean implementation class MessageManager
 */
@Stateless
@LocalBean
@Path("message")
public class MessageManager 
{
	@Inject
	private MessageRepository messageRepo;
	
	@Inject
	AgentManager agentManager;
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public AgentEntity testREST() throws MalformedURLException
	{
		System.out.println("MessageManager#testREST");
		AgentIdentifier aid = new AgentIdentifier("aid123");
		AgentType type = new AgentType("type111");
		AgentEntity ret = new AgentEntity(type, aid);
		
		return ret;
	}
	
	@POST
	@Path("envelope")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public void handleEnvelope(MessageEnvelope envelope)
	{
		// TODO
		// if stamped by us discard
		/*if(envelope.getReceived().contains(ourPlatformName))
		{
			return;
		}
		else*/
		{
			handleMessage(envelope.getContent());
		}
	}

	@POST
	@Path("acl")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public void handleMessage(ACLMessage message)
	{
		List<AgentIdentifier> local = new ArrayList<>();
		List<AgentIdentifier> remote = new ArrayList<>();
		List<AgentIdentifier> outbox = new ArrayList<>();
		
		// classify receivers
		message.getReceiverList().forEach((AgentIdentifier aid)->
		{
			if(agentManager.contains(aid)) local.add(aid);
			//else if(remoteRepo.containsAgent(aid)) remote.add(aid);
			else outbox.add(aid);
		});
		
		// set unread count
		message.setUnreadCount(local.size() + outbox.size());
		
		// persist if needed
		if(!local.isEmpty() || !outbox.isEmpty())
		{
			messageRepo.persist(message);
		}
		
		// if agent is local send to local agent
		local.forEach((AgentIdentifier aid)-> sendToLocal(aid, message));
		// if we can locate agent on a remote platform send to remote platform
		remote.forEach((AgentIdentifier aid)-> sendToRemote(aid, message));
		// if the receiver cannot be located leave message in outbox for manual retrieval
		outbox.forEach((AgentIdentifier aid)-> sendToOutbox(aid, message));
	}

	private void sendToLocal(AgentIdentifier aid, ACLMessage message)
    {
    	// link messages to agents and notify them
    	agentManager.deliverMessage(aid, message);
    }
    
    private void sendToRemote(AgentIdentifier aid, ACLMessage message)
    {
    	// TODO
    	// wrap into envelope
    }
    
    private void sendToOutbox(AgentIdentifier aid, ACLMessage message)
    {
    	// TODO
    }
    
    private void messageReceivedEventHandler()
    {
    	// TODO
    	/*if()
    	{
    		messageRepo.remove(message);
    	}*/
    }
}
