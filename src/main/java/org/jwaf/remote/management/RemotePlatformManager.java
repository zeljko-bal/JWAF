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
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.remote.exception.AgentTransportFailed;
import org.jwaf.remote.exception.AgentTransportSuccessful;
import org.jwaf.remote.persistence.entity.AgentPlatform;
import org.jwaf.remote.persistence.repository.RemotePlatformRepository;


@Stateless
@LocalBean
public class RemotePlatformManager
{
	@Inject
	private RemotePlatformRepository repo;
	
	@Inject
	private AgentManager agentManager;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	public AgentPlatform locationOf(String agentName)
	{
		return repo.locationOf(agentName);
	}
	
	public AgentPlatform findPlatform(String name)
	{
		return repo.findPlatform(name);
	}
	
	public AgentIdentifier findAid(String name)
	{
		return repo.findAid(name);
	}
	
	public boolean containsPlatform(String name)
	{
		return repo.containsPlatform(name);
	}
	
	public boolean containsAid(String name)
	{
		return repo.containsAid(name);
	}
	
	public boolean containsAid(String name, String platformName)
	{
		return repo.containsAid(name, platformName);
	}
	
	public void registerPlatform(String name, URL url)
	{
		repo.registerPlatform(new AgentPlatform(name, url));
	}
	
	public void registerAid(AgentIdentifier aid, String platformName)
	{
		repo.register(aid, platformName);
	}
	
	public void unregisterPlatform(String platformName)
	{
		repo.unregisterPlatform(platformName);
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
	
	public void sendAgent(String agentName, String platformName) throws AgentTransportSuccessful, AgentTransportFailed
	{
		AgentEntity agent = agentManager.depart(agentName);
		
		URL address = findPlatform(platformName).getAddress();
		
		// if response is http ok (200)
		if(ClientBuilder.newClient().target(address.toString()).path("remote").path("receive").request().post(Entity.xml(agent)).getStatus() == 200)
		{
			agentManager.departed(agentName);
			ClientBuilder.newClient().target(address.toString()).path("remote").path("arrived").request().post(Entity.xml(agentName));
			throw new AgentTransportSuccessful();
		}
		else
		{
			agentManager.cancelDeparture(agentName);
			throw new AgentTransportFailed();
		}
	}
	
	public boolean receiveRemoteAgent(AgentEntity agent)
	{
		return agentManager.receiveAgent(agent);
	}
	
	public void arrived(String agentName)
	{
		agentManager.arrived(agentName);
	}
}
