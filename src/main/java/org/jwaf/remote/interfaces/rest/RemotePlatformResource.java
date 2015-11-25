package org.jwaf.remote.interfaces.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.remote.AgentTransportData;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.remote.persistence.entity.AgentPlatform;

@Path("remote")
@Stateless
public class RemotePlatformResource
{
	@Inject
	private RemotePlatformManager remoteManager;
	
	/*
	 * information
	 */
	
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
	@Path("platform/register/{name}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response registerPlatform(@PathParam("name") String name, String url) throws MalformedURLException
	{
		remoteManager.registerPlatform(name, new URL(url));
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
		GenericEntity<List<AgentPlatform>> entity = new GenericEntity<List<AgentPlatform>>(remoteManager.retrievePlatforms()) {};
		return Response.ok(entity).build();
	}
	
	@GET
	@Path("aid/find_all/{platformName}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response retrieveAgentIds(@PathParam("platformName") String platformName)
	{
		GenericEntity<List<AgentIdentifier>> entity = new GenericEntity<List<AgentIdentifier>>(remoteManager.retrieveAgentIds(platformName)) {};
		return Response.ok(entity).build();
	}
	
	/*
	 * agent transfer
	 */
	
	@POST
	@Path("receive")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response receiveRemoteAgent(AgentTransportData transportData)
	{
		remoteManager.receiveRemoteAgent(transportData);
		return Response.ok().build();
	}
	
	@POST
	@Path("received/{platform_name}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response agentReceived(@PathParam("platform_name") String platformName, String agentName)
	{
		remoteManager.agentReceived(agentName, platformName);
		return Response.ok().build();
	}
	
	@POST
	@Path("not_received")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response agentNotReceived(String agentName)
	{
		remoteManager.agentNotReceived(agentName);
		return Response.ok().build();
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
