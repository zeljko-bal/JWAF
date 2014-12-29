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
import javax.ws.rs.core.Response;

import org.jwaf.agent.annotation.event.AgentInitializedEvent;
import org.jwaf.agent.annotation.event.AgentRemovedEvent;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.remote.persistence.entity.AgentPlatform;
import org.jwaf.remote.persistence.repository.RemotePlatformRepository;


@Stateless
@LocalBean
public class RemotePlatformManager
{
	@Inject
	private RemotePlatformRepository repo;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
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
	
	public void sendAgent(String agentName, String platformName)
	{
		AgentEntity agent = null;
		// TODO find AgentEntity, retrieve, set in transit
		
		URL address = findPlatform(platformName).getAddress();
		
		Client client = ClientBuilder.newClient();
		client.target(address.toString()).path("remote").path("receive").request().post(Entity.xml(agent));
		
		// TODO remove agent if successfull and throw transported, else set agent to active and throw transport failed
	}
	
	public boolean willAcceptAgent(AgentIdentifier aid)
	{
		// TODO willAcceptAgent implementation
		return true;
	}
	
	public boolean willRemoteAcceptAgent(AgentIdentifier aid, String platformName)
	{
		URL address = findPlatform(platformName).getAddress();
		
		Client client = ClientBuilder.newClient();
		Response resp =  client.target(address.toString()).path("remote").path("will_accept").request().post(Entity.xml(aid));
		
		if(resp.getEntity() instanceof String)
		{
			return new Boolean((String) resp.getEntity());
		}
		
		return false;
	}
	
	public void receiveRemoteAgent(AgentEntity agent)
	{
		agent.setHasNewMessages(!agent.getMessages().isEmpty());
		// TODO send to agent manager for initialization
	}
}
