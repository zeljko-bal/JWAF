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

import org.jwaf.agent.annotations.events.AgentInitializedEvent;
import org.jwaf.agent.annotations.events.AgentRemovedEvent;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.platform.annotations.resource.LocalPlatformAddress;
import org.jwaf.platform.annotations.resource.LocalPlatformName;
import org.jwaf.remote.AgentTransportData;
import org.jwaf.remote.exceptions.AgentDeparted;
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
	
	@Inject
	private AidManager aidManager;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
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
		// automatic registering not workimg, disbled for now
		/*
		retrievePlatforms().forEach((AgentPlatform platform) -> 
		{
			Client client = ClientBuilder.newClient();
			client.target(platform.getAddress().toString()).path("remote").path("aid").path("register").path(localPlatformName).request().post(Entity.xml(aid));
		});*/
	}
	
	public void agentRemovedEventHandler(@Observes @AgentRemovedEvent AgentIdentifier aid)
	{
		retrievePlatforms().forEach((AgentPlatform platform) -> 
		{
			Client client = ClientBuilder.newClient();
			client.target(platform.getAddress().toString()).path("remote").path("aid").path(localPlatformName).path(aid.getName()).request().delete();
		});
	}
	
	/*
	 * transport
	 */
	
	public void sendAgent(String agentName, String platformName, String serializedData) 
			throws AgentDeparted
	{
		AgentEntity agent = agentManager.depart(agentName);
		AgentTransportData transportData = new AgentTransportData(agent, serializedData, localPlatformName, localPlatformAddress);

		URL address = findPlatform(platformName).getAddress();
		
		// receive agent on a remote platform
		ClientBuilder.newClient().target(address.toString())
				.path("remote")
				.path("receive")
				.request().post(Entity.xml(transportData));
		
		throw new AgentDeparted("agent <" + agentName + "> traveling to <"+platformName+">.");
	}
	
	public void agentReceived(String agentName, String originPlatform)
	{
		agentManager.transported(agentName, originPlatform);
		
		URL address = findPlatform(originPlatform).getAddress();
		
		ClientBuilder.newClient().target(address.toString())
				.path("remote")
				.path("arrived")
				.request().post(Entity.text(agentName));
	}
	
	public void agentNotReceived(String agentName)
	{
		agentManager.cancelDeparture(agentName);
	}
	
	public void receiveRemoteAgent(AgentTransportData transportData)
	{
		try
		{
			agentManager.receiveAgent(transportData.getAgent(), transportData.getSerializedData());
			
			ClientBuilder.newClient().target(transportData.getPlatformAddress().toString())
					.path("remote")
					.path("received")
					.path(transportData.getPlatformName())
					.request().post(Entity.text(transportData.getAgent().getAid().getName()));
		}
		catch(Exception e)
		{
			ClientBuilder.newClient().target(transportData.getPlatformAddress().toString())
					.path("remote")
					.path("not_received")
					.request().post(Entity.text(transportData.getAgent().getAid().getName()));
		}
	}
	
	public void arrived(String agentName)
	{
		agentManager.arrived(agentName);
	}
}
