package org.jwaf.event.management;

import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Path;

import org.jwaf.agent.annotation.LocalPlatformAid;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.event.persistence.entity.EventEntity;
import org.jwaf.event.persistence.repository.EventRepository;
import org.jwaf.event.processor.EventProcessor;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.performative.PlatformPerformative;
import org.jwaf.platform.annotation.resource.AgentJNDIPrefix;

@Stateless
@LocalBean
@Path("event")
public class EventManager
{
	@Inject
	private EventRepository eventRepo;
	
	@Inject @LocalPlatformAid
	private AgentIdentifier localPlatformAid;

	@Inject
	private MessageSender messageSender;

	@Inject @AgentJNDIPrefix
	private String agentJNDIPrefix;

	public void register(String name)
	{
		eventRepo.register(name);
	}

	public void register(String name, String type)
	{
		eventRepo.register(name, type);
	}

	public void unregister(String name)
	{
		eventRepo.unregister(name);
	}

	public void subscribe(String agentName, String eventName)
	{
		eventRepo.subscribe(agentName, eventName);
	}

	public void unsubscribe(String agentName, String eventName)
	{
		eventRepo.unsubscribe(agentName, eventName);
	}

	public void fire(String eventName, String content)
	{
		EventEntity event = eventRepo.find(eventName);

		EventProcessor processor = findEventProcessor(event.getType());

		String contentToSend = content;

		if(processor != null)
		{
			contentToSend = processor.process(contentToSend);
		}
		
		sendEventMessage(event, new ACLMessage().setContent(contentToSend));
	}
	
	public void fire(String eventName, Serializable content)
	{
		EventEntity event = eventRepo.find(eventName);

		EventProcessor processor = findEventProcessor(event.getType());

		Serializable contentToSend = content;

		if(processor != null)
		{
			contentToSend = processor.process(contentToSend);
		}
		
		sendEventMessage(event, new ACLMessage().setContentAsObject(contentToSend));
	}

	private void sendEventMessage(EventEntity event, ACLMessage message)
	{
		message.setPerformative(PlatformPerformative.EVENT_MESSAGE);
		message.setSender(localPlatformAid);
		message.getReceiverList().addAll(event.getRegisteredAgents());
		message.getUserDefinedParameters().put("EVENT_NAME", event.getName());
		
		messageSender.send(message);
	}

	private EventProcessor findEventProcessor(String type)
	{
		try
		{
			return (EventProcessor)(new InitialContext()).lookup(agentJNDIPrefix + type);
		}
		catch (NamingException e)
		{
			return null;
		}
		catch (Exception e)
		{
			// TODO log findEventProcessor Exception
			return null;
		}
	}
}
