package org.jwaf.remote.interfaces.rest;

import java.net.URL;

import javax.ejb.Stateless;
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

import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.management.RemotePlatformManager;

@Path("remote")
@Stateless
public class RemotePlatformResource
{
	@Inject
	private RemotePlatformManager remoteManager;
	
	@GET
	@Path("platform/find/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response findPlatform(@PathParam("name") String name)
	{
		return Response.ok(remoteManager.findPlatform(name)).build();
	}
	
	@GET
	@Path("aid/find/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response findAid(@PathParam("name") String name)
	{
		return Response.ok(remoteManager.findAid(name)).build();
	}
	
	@GET
	@Path("platform/contains/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response containsPlatform(@PathParam("name") String name)
	{
		return Response.ok(Boolean.toString(remoteManager.containsPlatform(name))).build();
	}
	
	@GET
	@Path("aid/contains/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response containsAid(@PathParam("name") String name)
	{
		return Response.ok(remoteManager.containsAid(name)).build();
	}
	
	@POST
	@Path("platform/register/{name}/{url}")
	public Response registerPlatform(@PathParam("name") String name, @PathParam("url") URL url)
	{
		remoteManager.registerPlatform(name, url);
		return Response.ok().build();
	}
	
	@POST
	@Path("aid/register/{name}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response registerAid(AgentIdentifier aid, @PathParam("name") String platformName)
	{
		remoteManager.registerAid(aid, platformName);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("platform/{name}")
	public Response unregisterPlatform(@PathParam("name") String platformName)
	{
		remoteManager.unregisterPlatform(platformName);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("aid/{platformName}/{agentName}")
	public Response unregisterAid(@PathParam("platformName") String platformName, @PathParam("agentName") String agentName)
	{
		remoteManager.unregisterAid(platformName, agentName);
		return Response.ok().build();
	}
	
	@GET
	@Path("platform/find_all")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response retrievePlatforms()
	{
		return Response.ok(remoteManager.retrievePlatforms()).build();
	}
	
	@GET
	@Path("aid/find_all/{platformName}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response retrieveAgentIds(@PathParam("platformName") String platformName)
	{
		return Response.ok(remoteManager.retrieveAgentIds(platformName)).build();
	}
	
	@POST
	@Path("receive")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response receiveRemoteAgent(AgentEntity agent)
	{
		if(remoteManager.receiveRemoteAgent(agent))
		{
			return Response.ok().build();
		}
		else
		{
			return Response.notModified().build();
		}
	}
	
	@POST
	@Path("arrived")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response agentArrived(String agentName)
	{
		remoteManager.arrived(agentName);
		return Response.ok().build();
	}
}
