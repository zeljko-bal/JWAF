package org.jwaf.remote.management;

import java.net.URL;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jwaf.agent.annotation.event.AgentCreatedEvent;
import org.jwaf.agent.annotation.event.AgentRemovedEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.remote.persistence.entity.AgentPlatform;
import org.jwaf.remote.persistence.repository.RemotePlatformRepository;


@Stateless
@LocalBean
@Path("remote")
public class RemotePlatformManager
{
	@Inject
	RemotePlatformRepository repo;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@GET
	@Path("platform/find/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public AgentPlatform find(@PathParam("name") String name)
	{
		return repo.find(name);
	}
	
	@GET
	@Path("aid/find/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public AgentIdentifier findAid(@PathParam("name") String name)
	{
		return repo.findAid(name);
	}
	
	@GET
	@Path("platform/contains/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public boolean contains(@PathParam("name") String name)
	{
		return repo.contains(name);
	}
	
	@GET
	@Path("aid/contains/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public boolean containsAid(@PathParam("name") String name)
	{
		return repo.containsAid(name);
	}
	
	@POST
	@Path("platform/register/{name}/{url}")
	public void register(@PathParam("name") String name, @PathParam("url") URL url)
	{
		repo.register(new AgentPlatform(name, url));
	}
	
	@POST
	@Path("aid/register/{name}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public void register(AgentIdentifier aid, @PathParam("name") String platformName)
	{
		repo.register(aid, platformName);
	}
	
	@DELETE
	@Path("platform/{name}")
	public void unregister(@PathParam("name") String platformName)
	{
		repo.unregister(platformName);
	}
	
	@DELETE
	@Path("aid/{platformName}/{agentName}")
	public void unregister(@PathParam("platformName") String platformName, @PathParam("agentName") String agentName)
	{
		repo.unregister(agentName, platformName);
	}
	
	@GET
	@Path("platform/find_all")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<AgentPlatform> retrievePlatforms()
	{
		return repo.retrievePlatforms();
	}
	
	@GET
	@Path("aid/find_all/{platformName}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<AgentIdentifier> retrieveAgentIds(@PathParam("platformName") String platformName)
	{
		return repo.retrieveAgentIds(platformName);
	}
	
	public void agentCreatedEventHandler(@Observes @AgentCreatedEvent AgentIdentifier aid)
	{
		retrievePlatforms().forEach((AgentPlatform platform) -> 
		{
			Client client = ClientBuilder.newClient();
			client.target(platform.getAddress().toString()).path("remote").path("aid").path("register").path(localPlatformName).request().post(Entity.xml(aid));
		});
	}
	
	public void agentRemovedEventHandler(@Observes @AgentRemovedEvent AgentIdentifier aid)
	{
		retrievePlatforms().forEach((AgentPlatform platform) -> 
		{
			Client client = ClientBuilder.newClient();
			client.target(platform.getAddress().toString()).path("remote").path("aid").path(localPlatformName).path(aid.getName()).request().delete();
		});
	}
}
