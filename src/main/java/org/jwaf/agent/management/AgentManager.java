package org.jwaf.agent.management;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentEntityView;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.agent.persistence.repository.AgentTypeRepository;
import org.jwaf.message.management.MessageManager;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotations.LocalPlatformAddress;
import org.jwaf.platform.annotations.LocalPlatformName;

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
	private AgentExecutor executor;
	
	@Inject
	private MessageManager messageManager;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
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
		
		AgentEntity newAgent = new AgentEntity(type, aid);
		
		// persist agent
		agentRepo.create(newAgent);
		
		// invoke custom setup method
		executor.setup(aid, request.getType());
		
		// set state to passive
		agentRepo.passivate(aid, true);
		
		return aid;
	}
	
	@DELETE
	@Path("{name}")
	public Response deleteAgent(@PathParam("name") String name)
	{
		ACLMessage message = new ACLMessage(PlatformPerformative.SELF_DELETE, getPlatformAid());
		message.getReceiverList().add(find(name).getAid());
		
		messageManager.handleMessage(message);
		
		return Response.ok().build();
	}
	
	public AgentEntityView find(String name)
	{
		return agentRepo.findView(name);
	}

	public AgentEntityView find(AgentIdentifier aid)
	{
		return find(aid.getName());
	}

	public void deliverMessage(AgentIdentifier aid, ACLMessage message)
	{
		String prevState = null;

		// activate agent and get previous state
		prevState = agentRepo.activate(aid, message);

		// if agent was passive activate him
		if(AgentState.PASSIVE.equals(prevState))
		{
			String agentTypeName = find(aid).getType().getName();

			// execute asynchronously
			try
			{
				//execService.submit(new AgentExec(aid, agentTypeName));
				executor.execute(aid, agentTypeName);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				// if service submit failed force agent to passivate
				agentRepo.passivate(aid, true);
			}
		}
	}

	public boolean contains(AgentIdentifier aid)
	{
		return agentRepo.contains(aid);
	}

	@GET
	@Path("contains/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public boolean contains(@PathParam("name") String name)
	{
		return agentRepo.contains(name);
	}

	public AgentType getTypeOf(AgentIdentifier aid)
	{
		return find(aid).getType();
	}

	@GET
	@Path("type/{name}")
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
	@Path("public_data")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Map<String, String> getPublicData(String agentName)
	{
		return agentRepo.getPublicData(agentName);
	}
	
	public AgentIdentifier getPlatformAid()
	{
		return agentRepo.getPlatformAid();
	}
}
