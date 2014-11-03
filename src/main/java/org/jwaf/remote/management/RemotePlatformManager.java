package org.jwaf.remote.management;

import java.net.URL;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

import org.jwaf.agent.annotation.event.AgentInitializedEvent;
import org.jwaf.agent.annotation.event.AgentRemovedEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.remote.persistence.entity.AgentPlatform;
import org.jwaf.remote.persistence.repository.RemotePlatformRepository;


@Stateless
@LocalBean
public class RemotePlatformManager
{
	@Inject
	RemotePlatformRepository repo;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	public AgentPlatform find(String name)
	{
		return repo.find(name);
	}
	
	public AgentIdentifier findAid(String name)
	{
		return repo.findAid(name);
	}
	
	public boolean contains(String name)
	{
		return repo.contains(name);
	}
	
	public boolean containsAid(String name)
	{
		return repo.containsAid(name);
	}
	
	public void register(String name, URL url)
	{
		repo.register(new AgentPlatform(name, url));
	}
	
	public void registerAid(AgentIdentifier aid, String platformName)
	{
		repo.register(aid, platformName);
	}
	
	public void unregister(String platformName)
	{
		repo.unregister(platformName);
	}
	
	public void unregisterAid(String platformName, String agentName)
	{
		repo.unregister(agentName, platformName);
	}
	
	public List<AgentPlatform> retrievePlatforms()
	{
		return repo.retrievePlatforms();
	}
	
	public List<AgentIdentifier> retrieveAgentIds(String platformName)
	{
		return repo.retrieveAgentIds(platformName);
	}
	
	public void agentInitializedEventHandler(@Observes @AgentInitializedEvent AgentIdentifier aid)
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
