package org.jwaf.agent.management;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jwaf.agent.annotation.LocalPlatformAid;
import org.jwaf.agent.annotation.event.AgentInitializedEvent;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentEntityView;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.agent.persistence.repository.AgentTypeRepository;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.performative.PlatformPerformative;
import org.jwaf.platform.annotation.resource.LocalPlatformAddress;
import org.jwaf.platform.annotation.resource.LocalPlatformName;

/**
 * Session Bean implementation class AgentManager
 */
@Stateless
@LocalBean
@Path("agent")
public class AgentManager 
{
	@Inject
	private AgentRepository agentRepo;
	
	@Inject
	private AgentTypeRepository typeRepo;
	
	@Inject
	private AgentActivator activator;
	
	@Inject
	private MessageSender messageSender;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
	@Inject @AgentInitializedEvent
	private Event<AgentIdentifier> agentInitializedEvent;
	
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public AgentIdentifier createAgent(CreateAgentRequest request)
	{
		AgentType type = null;
		
		try
		{
			type = typeRepo.find(request.getType());
		}
		catch (NoResultException e)
		{
			e.printStackTrace();
			// TODO throw type doesnt exist
		}
		
		// new aid with name : random-uuid@local-platform-name
		AgentIdentifier aid = new AgentIdentifier(UUID.randomUUID().toString()+"@"+localPlatformName);
		aid.getAddresses().add(localPlatformAddress);
		aid.getUserDefinedParameters().putAll(request.getParams());
		aid.setRefCount(1);
		
		AgentEntity newAgent = new AgentEntity(type, aid);
		
		// persist agent
		agentRepo.create(newAgent);
		
		// invoke custom setup method
		activator.setup(aid, request.getType());
		
		// set state to passive
		agentRepo.passivate(aid, true);
		
		agentInitializedEvent.fire(aid);
		
		return aid;
	}
	
	@DELETE
	@Path("{name}")
	@Inject
	public Response requestAgentTermination(@PathParam("name") String name)
	{
		ACLMessage message = new ACLMessage(PlatformPerformative.SELF_TERMINATE, getPlatformAid());
		message.getReceiverList().add(find(name).getAid());
		
		messageSender.send(message);
		
		return Response.ok().build();
	}

	@GET
	@Path("contains/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public boolean contains(@PathParam("name") String name)
	{
		return agentRepo.contains(name);
	}

	@GET
	@Path("type/of/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public AgentType getTypeOf(@PathParam("name") String name)
	{
		return find(name).getType();
	}
	
	@GET
	@Path("type/info/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public AgentType findType(@PathParam("name") String name)
	{
		return typeRepo.find(name);
	}
	
	@POST
	@Path("type/info")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public void createType(AgentType type)
	{
		typeRepo.create(type);
	}
	
	@DELETE
	@Path("type/info/{name}")
	public void removeType(@PathParam("name") String name)
	{
		typeRepo.remove(name);
	}
	
	@GET
	@Path("public_data/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Map<String, String> getPublicData(@PathParam("name") String agentName)
	{
		return agentRepo.getPublicData(agentName);
	}
	
	@GET
	@Path("platform_aid")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@javax.enterprise.inject.Produces @LocalPlatformAid
	public AgentIdentifier getPlatformAid()
	{
		return agentRepo.getPlatformAid();
	}
	
	private AgentEntityView find(String name)
	{
		return agentRepo.findView(name);
	}
}
