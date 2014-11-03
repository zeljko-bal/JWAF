package org.jwaf.event.interfaces.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jwaf.event.management.EventManager;

@Path("event")
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
	@Path("exists/{eventName}")
	public boolean exists(@PathParam("eventName") String eventName)
	{
		return eventManager.exists(eventName);
	}
	
	@POST
	@Path("info/{eventName}")
	public void register(@PathParam("eventName") String eventName)
	{
		eventManager.register(eventName);
	}

	@POST
	@Path("info/{eventName}/{type}")
	public void register(@PathParam("eventName") String eventName, @PathParam("type") String type)
	{
		eventManager.register(eventName, type);
	}

	@DELETE
	@Path("info/{eventName}")
	public void unregister(@PathParam("eventName") String name)
	{
		eventManager.unregister(name);
	}

	@POST
	@Path("subscriber/{agentName}/{eventName}")
	public void subscribe(@PathParam("agentName") String agentName, @PathParam("eventName") String eventName)
	{
		eventManager.subscribe(agentName, eventName);
	}
	
	@DELETE
	@Path("subscriber/{agentName}/{eventName}")
	public void unsubscribe(@PathParam("agentName") String agentName, @PathParam("eventName") String eventName)
	{
		eventManager.unsubscribe(agentName, eventName);
	}
}
