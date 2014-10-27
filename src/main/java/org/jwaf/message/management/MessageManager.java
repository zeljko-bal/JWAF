package org.jwaf.message.management;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.DataStoreType;
import org.jwaf.message.FIPA.FIPAPerformative;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.message.persistence.entity.MessageEnvelope;
import org.jwaf.message.persistence.repository.MessageRepository;
import org.jwaf.platform.LocalPlatform;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.util.XMLSchemaUtils;

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
	private AgentManager agentManager;
	
	@Inject 
	private LocalPlatform localPlatform;
	
	@Inject
	private RemotePlatformManager remoteManager;
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response testREST() throws MalformedURLException
	{
		AgentIdentifier testAid = new AgentIdentifier("test1@platform1");
		
		ACLMessage testMessage = new ACLMessage();
		testMessage.getReceiverList().add(testAid);
		
		handleMessage(testMessage);
		
		
		System.out.println("MessageManager#testREST");
		AgentIdentifier aid = new AgentIdentifier("aid123");
		AgentType type = new AgentType("type111");
		AgentEntity ret = new AgentEntity(type, aid);
		
		ret.getData(DataStoreType.PUBLIC).put("key1", "val1");
		ret.getMessages().add((new ACLMessage(FIPAPerformative.DISCONFIRM, ret.getAid())).setContent("content"));
		
		
		MessageEnvelope env = new MessageEnvelope();
		env.setContent((new ACLMessage(FIPAPerformative.DISCONFIRM, ret.getAid())).setContent("content"));
		
		return Response.ok(env).build();
	}
	
	@POST
	@Path("envelope")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response handleEnvelope(MessageEnvelope envelope)
	{		
		// if stamped by this platform discard
		if(envelope.getReceived().contains(localPlatform.getName()))
		{
			return Response.notAcceptable(null).build();
		}
		else
		{
			// unwrap and handle
			handleMessage(envelope.getContent(), envelope);
			return Response.accepted().build();
		}		
	}

	@POST
	@Path("acl")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response handleMessage(ACLMessage message)
	{
		// handle message without an envelope
		handleMessage(message, null);
		return Response.accepted().build();
	}
	
	private void handleMessage(ACLMessage message, MessageEnvelope originalEnvelope)
	{
		List<AgentIdentifier> local = new ArrayList<>();
		List<AgentIdentifier> remote = new ArrayList<>();
		List<AgentIdentifier> outbox = new ArrayList<>();
		
		List<AgentIdentifier> recievers;
		
		recievers = obtainReceivers(message, originalEnvelope);
		
		// classify receivers
		recievers.forEach((AgentIdentifier aid)->
		{
			if(agentManager.contains(aid))
			{
				// if agent is local
				local.add(aid);
			}
			else if(!aid.getAddresses().isEmpty())
			{
				// else if agent has addresses
				remote.add(aid);
			}
			else if(remoteManager.containsAid(aid.getName()))
			{
				// else if remoteManager has addresses for this agent
				aid.getAddresses().addAll(remoteManager.findAid(aid.getName()).getAddresses());
				remote.add(aid);
			}
			else
			{
				// else leave in outbox
				outbox.add(aid);
			}
		});

		// persist if needed
		if(!local.isEmpty() || !outbox.isEmpty())
		{
			// set unread count
			message.setUnreadCount(local.size() + outbox.size());
			
			messageRepo.persist(message);
		}
		
		// if agent is local send to local agent
		local.forEach((AgentIdentifier aid)-> sendToLocal(aid, message));
		// if we can locate agent on a remote platform send to remote platform
		remote.forEach((AgentIdentifier aid)-> sendToRemote(aid, message, originalEnvelope));
		// if the receiver cannot be located leave message in outbox for manual retrieval
		outbox.forEach((AgentIdentifier aid)-> sendToOutbox(aid, message));
	}

	private List<AgentIdentifier> obtainReceivers(ACLMessage message, MessageEnvelope originalEnvelope)
	{
		if(originalEnvelope == null)
		{
			// from message if originalEnvelope is not provided
			return message.getReceiverList();
		}
		else
		{
			// if Intended_receiverList is empty populate from ToList
			if(originalEnvelope.getIntended_receiverList().isEmpty())
			{
				originalEnvelope.getIntended_receiverList().addAll(originalEnvelope.getToList());
			}
			
			return originalEnvelope.getIntended_receiverList();
		}
	}

	private void sendToLocal(AgentIdentifier aid, ACLMessage message)
    {
    	// link messages to agents and notify them
		// TODO maybe async/event
    	agentManager.deliverMessage(aid, message);
    }
    
    private void sendToRemote(AgentIdentifier aid, ACLMessage message, MessageEnvelope originalEnvelope)
    {
    	// obtain the address
    	URL address = null;
    	if(!aid.getAddresses().isEmpty())
    	{
    		address = aid.getAddresses().get(0);
    	}
    	else
    	{
    		address = remoteManager.findAid(aid.getName()).getAddresses().get(0);
    	}
    	
    	// wrap into envelope
    	MessageEnvelope envelope;
    	if(originalEnvelope != null)
    	{
    		// if provided use original envelope
    		envelope = originalEnvelope;
    		envelope.getIntended_receiverList().clear();
    	}
    	else
    	{
    		// if not create new 
    		envelope = new MessageEnvelope();
    		envelope.setDate(new Date());
    		envelope.setContent(message);
    		envelope.getToList().addAll(message.getReceiverList());
    		envelope.setFrom(message.getSender());
    	}
    	
    	// stamp the envelope
    	envelope.getReceived().add(localPlatform.getName());
    	
    	// set reciever
    	envelope.getIntended_receiverList().add(aid);
    	
    	// call remote service
    	Client client = ClientBuilder.newClient();
    	client.target(address.toString()).path("message").path("envelope").request().post(Entity.xml(envelope));
    }
    
    private void sendToOutbox(AgentIdentifier aid, ACLMessage message)
    {
    	// TODO sendToOutbox
    }
    /*
    private void messageReceivedEventHandler()
    {
    	// TODO messageReceivedEventHandler
    	if()
    	{
    		messageRepo.remove(message);
    	}
    }*/
    
	@GET
	@Path("/acl/schema")
	@Produces(MediaType.APPLICATION_XML)
	public Response getAclSchema() throws IOException, JAXBException
	{		
		return Response.ok(XMLSchemaUtils.generate(ACLMessage.class)).build();
	}
	
	@GET
	@Path("/envelope/schema")
	@Produces(MediaType.APPLICATION_XML)
	public Response getEnvelopeSchema() throws IOException, JAXBException
	{
		return Response.ok(XMLSchemaUtils.generate(MessageEnvelope.class)).build();
	}
}
