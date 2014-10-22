package org.jwaf.message;

import java.io.IOException;
import java.io.StringWriter;
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
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.jwaf.agent.AgentManager;
import org.jwaf.agent.entity.AgentEntity;
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.agent.entity.AgentType;
import org.jwaf.agent.persistence.DataStoreType;
import org.jwaf.message.FIPA.FIPAPerformative;
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
	public Response testREST() throws MalformedURLException
	{
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
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("schema")
	public Response testRESTgetSchema() throws IOException, JAXBException
	{
		StringWriter writer = new StringWriter();  
		
		JAXBContext jaxbContext = JAXBContext.newInstance(MessageEnvelope.class);
		
		jaxbContext.generateSchema(new SchemaOutputResolver()
		{
		    @Override  
		    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {  
		        final StreamResult result = new StreamResult(writer);  
		        result.setSystemId("no-id"); // Result MUST contain system id, or JAXB throws an error message  
		        return result;  
		    }
		});
		
		return Response.ok(writer.toString()).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response testRESTPosty(AgentEntity agent)
	{
		System.out.println("MessageManager#testRESTPosty");
		System.out.println("agent: " + agent.getAid().getName() + ", of type: " + agent.getType().getName() + ", with message: " + agent.getMessages().get(0).getContent());
		
		return Response.accepted(agent).build();
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
    /*
    private void messageReceivedEventHandler()
    {
    	// TODO
    	if()
    	{
    		messageRepo.remove(message);
    	}
    }*/
}
