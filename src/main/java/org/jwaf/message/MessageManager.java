package org.jwaf.message;

import java.net.MalformedURLException;
import java.net.URL;
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
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.message.entity.ACLMessage;

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
	public AgentIdentifier testREST() throws MalformedURLException
	{
		System.out.println("MessageManager#testREST");
		AgentIdentifier ret = new AgentIdentifier("aid123");
		ret.getUserDefinedParameters().put("asd", "dfg");
		ret.getUserDefinedParameters().put("adfgsd", "ddfgfg");
		
		AgentIdentifier resolver = new AgentIdentifier("fdgdfg");
		ret.getResolvers().add(resolver);
		
		ret.getAddresses().add(new URL("http://example.com/pages/"));
		
		return ret;
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void handleMessage(ACLMessage message)
	{
		// set unread count
		message.setUnreadCount(message.getReceiverList().size());
		
		List<AgentIdentifier> local = new ArrayList<>();
		List<AgentIdentifier> remote = new ArrayList<>();
		List<AgentIdentifier> outbox = new ArrayList<>();
		
		// classify receivers
		message.getReceiverList().forEach((AgentIdentifier aid)->
		{
			if(agentManager.contains(aid))
			{
				local.add(aid);
			}
			/*else if(remoteRepo.containsAgent(aid))
			{
				remote.add(aid);
				// if we can locate agent on a remote platform
				// TODO send to remote platform
				sendToRemote(aid, message);
			}*/
			else
			{
				outbox.add(aid);
				
				// if the receiver cannot be located leave message in outbox for manual retrieval
				sendToOutbox(aid, message);
			}
		});
		
		// persist if needed
		if(!local.isEmpty() || !outbox.isEmpty())
		{
			messageRepo.persist(message);
		}
		
		// send out
		local.forEach((AgentIdentifier aid)-> sendToLocal(aid, message));
		remote.forEach((AgentIdentifier aid)-> sendToRemote(aid, message));
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
