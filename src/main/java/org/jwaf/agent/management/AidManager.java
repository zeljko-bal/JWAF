package org.jwaf.agent.management;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jwaf.agent.annotations.events.AgentRemovedEvent;
import org.jwaf.agent.annotations.events.AidRemovedEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AidRepository;
import org.jwaf.platform.annotations.resource.LocalPlatformAddress;
import org.jwaf.platform.annotations.resource.LocalPlatformName;
import org.jwaf.remote.management.RemotePlatformManager;

/**
 * A bean that contains the methods for {@link AgentIdentifier} management.
 * 
 * @author zeljko.bal
 */
@Stateless
@LocalBean
public class AidManager
{
	@Inject
	private AidRepository aidRepository;
	
	@Inject
	private RemotePlatformManager remotePlatformManager;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
	@Inject @AidRemovedEvent
	private Event<String> aidRemovedEvent;
	
	public AgentIdentifier find(String name)
	{
		return aidRepository.find(name);
	}
	
	public AgentIdentifier save(AgentIdentifier aid)
	{
		return aidRepository.save(aid);
	}
	
	public AgentIdentifier insert(AgentIdentifier aid)
	{
		return aidRepository.insert(aid);
	}

	public void remove(String name)
	{
		aidRepository.remove(name);
		aidRemovedEvent.fire(name);
	}
	
	public void addAddress(String name, URL address)
	{
		AgentIdentifier aid = find(name);
		aid.getAddresses().add(address);
		aidRepository.save(aid);
	}

	public void removeAddress(String name, URL address)
	{
		AgentIdentifier aid = find(name);
		aid.getAddresses().remove(address);
		aidRepository.save(aid);
	}

	public void addResolver(String name, AgentIdentifier resolver)
	{
		AgentIdentifier aid = find(name);
		aid.getResolvers().add(resolver);
		aidRepository.save(aid);
	}
	
	public void removeResolver(String name, AgentIdentifier resolver)
	{
		AgentIdentifier aid = find(name);
		aid.getResolvers().remove(resolver);
		aidRepository.save(aid);
	}
	
	public void changeLocation(String name, String newPlatform)
	{
		List<URL> mtAddresses;
		
		if(localPlatformName.equals(newPlatform))
		{
			mtAddresses = new ArrayList<>();
			mtAddresses.add(localPlatformAddress);
		}
		else
		{
			mtAddresses = remotePlatformManager.findPlatform(newPlatform).getMTAddresses();
		}
		
		aidRepository.changeMTAddresses(name, mtAddresses);
	}
	
	public void agentRemovedEventHandler(@Observes @AgentRemovedEvent AgentIdentifier aid)
	{
		remove(aid.getName());
	}
}
