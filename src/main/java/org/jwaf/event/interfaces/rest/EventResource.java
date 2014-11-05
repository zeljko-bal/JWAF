package org.jwaf.event.interfaces.rest;

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

import org.jwaf.event.management.EventManager;

@Path("event")
@Stateless
public class EventResource
{
	@Inject
	private EventManager eventManager;
	
	@POST
	@Path("fire/{eventName}")
	@Consumes(MediaType.TEXT_PLAIN)
	public void fire(@PathParam("eventName") String eventName, String content)
	{
		eventManager.fire(eventName, content);
	}
	
	@GET
	@Path("info/{eventName}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response exists(@PathParam("eventName") String eventName)
	{
		return Response.ok(Boolean.toString(eventManager.exists(eventName))).build();
	}
	
	@POST
	@Path("info/{eventName}")
	public Response register(@PathParam("eventName") String eventName)
	{
		eventManager.register(eventName);
		return Response.ok().build();
	}

	@POST
	@Path("info/{eventName}/{type}")
	public Response register(@PathParam("eventName") String eventName, @PathParam("type") String type)
	{
		eventManager.register(eventName, type);
		return Response.ok().build();
	}

	@DELETE
	@Path("info/{eventName}")
	public Response unregister(@PathParam("eventName") String name)
	{
		eventManager.unregister(name);
		return Response.ok().build();
	}

	@POST
	@Path("subscriber/{agentName}/{eventName}")
	public Response subscribe(@PathParam("agentName") String agentName, @PathParam("eventName") String eventName)
	{
		eventManager.subscribe(agentName, eventName);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("subscriber/{agentName}/{eventName}")
	public Response unsubscribe(@PathParam("agentName") String agentName, @PathParam("eventName") String eventName)
	{
		eventManager.unsubscribe(agentName, eventName);
		return Response.ok().build();
	}
}
