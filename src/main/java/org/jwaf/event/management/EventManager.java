package org.jwaf.event.management;

import java.io.Serializable;

import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
public class EventManager
{
	@Inject
	private EventRepository eventRepo;
	
	@Inject
	private MessageSender messageSender;
	
	@Inject @LocalPlatformAid
	private AgentIdentifier localPlatformAid;

	@Inject @AgentJNDIPrefix
	private String agentJNDIPrefix;

	public boolean exists(String name)
	{
		return eventRepo.exists(name);
	}
	
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

	@Asynchronous
	public void fire(String eventName, String content)
	{
		EventEntity event = eventRepo.find(eventName);

		EventProcessor processor = findEventProcessor(event.getType());

		String contentToSend = content;

		if(processor != null)
		{
			contentToSend = processor.process(contentToSend);
		}
		
		sendEventMessage(event, contentToSend);
	}
	
	@Asynchronous
	public void fire(String eventName, Serializable content)
	{
		EventEntity event = eventRepo.find(eventName);

		EventProcessor processor = findEventProcessor(event.getType());

		Serializable contentToSend = content;

		if(processor != null)
		{
			contentToSend = processor.process(contentToSend);
		}
		
		sendEventMessage(event, contentToSend);
	}

	private void sendEventMessage(EventEntity event, Serializable content)
	{
		ACLMessage message = new ACLMessage();
		
		message.setContentAsObject(content);
		message.setPerformative(PlatformPerformative.EVENT_MESSAGE);
		message.setSender(localPlatformAid);
		message.getReceiverList().addAll(event.getRegisteredAgents());
		message.getUserDefinedParameters().put(EventMessageProperties.EVENT_NAME, event.getName());
		
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
