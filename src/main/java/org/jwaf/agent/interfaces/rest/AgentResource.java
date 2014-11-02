package org.jwaf.agent.interfaces.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.CreateAgentRequest;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.agent.persistence.repository.AgentTypeRepository;

@Path("agent")
public class AgentResource
{
	@Inject
	private AgentRepository agentRepo;
	
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private AgentTypeRepository typeRepo;
	
	// /agent/
	
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response initialize(CreateAgentRequest request)
	{
		return Response.accepted(agentManager.initialize(request)).build();
	}
	
	@DELETE
	@Path("{name}")
	public Response requestTermination(@PathParam("name") String name)
	{
		agentManager.requestTermination(name);
		return Response.ok().build();
	}
	
	@GET
	@Path("contains/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response contains(@PathParam("name") String name)
	{
		return Response.ok(agentRepo.contains(name)).build();
	}
	
	@GET
	@Path("public_data/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getPublicData(@PathParam("name") String agentName)
	{
		return Response.ok(agentRepo.getPublicData(agentName)).build();
	}
	
	// /agent/type/
	
	@GET
	@Path("type/of/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getTypeOf(@PathParam("name") String name)
	{
		return Response.ok(agentRepo.findView(name).getType()).build();
	}
	
	@GET
	@Path("type/info/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response findType(@PathParam("name") String name)
	{
		return Response.ok(typeRepo.find(name)).build();
	}
	
	@POST
	@Path("type/info")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response createType(AgentType type)
	{
		typeRepo.create(type);
		return Response.accepted().build();
	}
	
	@DELETE
	@Path("type/info/{name}")
	public Response removeType(@PathParam("name") String name)
	{
		typeRepo.remove(name);
		return Response.ok().build();
	}
}
