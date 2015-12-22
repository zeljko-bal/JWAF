package org.jwaf.event.management;

import java.io.Serializable;

import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.agent.annotations.LocalPlatformAid;
import org.jwaf.agent.annotations.events.AidRemovedEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.event.exceptions.EventNotFound;
import org.jwaf.event.persistence.entity.EventEntity;
import org.jwaf.event.persistence.repository.EventRepository;
import org.jwaf.event.processor.EventProcessor;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotations.resource.EJBJNDIPrefix;
import org.slf4j.Logger;

/**
 * A management bean that contains event management methods.
 * 
 * @author zeljko.bal
 */
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
	
	@Inject @EJBJNDIPrefix
	private String ejbJNDIPrefix;
	
	@Inject
	private Logger log;
	
	public boolean exists(String name)
	{
		return eventRepo.exists(name);
	}
	
	public void register(String name)
	{
		eventRepo.register(name, null);
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
	
	public void onAidRemoved(@Observes @AidRemovedEvent String agentName)
	{
		eventRepo.unsubscribe(agentName);
	}
	
	/**
	 * Sends a message to all subscribers with the provided String content.
	 * The content is processed before sending with an {@link EventProcessor} if such bean exists.
	 * User defined property IS_EVENT of the message is set to true.
	 */
	@Asynchronous
	public void fire(String eventName, String content)
	{
		EventEntity event = eventRepo.find(eventName);
		
		assertEventExists(event, eventName);
		
		EventProcessor processor = findEventProcessor(event.getType());
		
		String contentToSend = content;
		
		if(processor != null)
		{
			contentToSend = processor.process(contentToSend).toString();
		}
		
		sendEventMessage(event, new ACLMessage().setContent(contentToSend));
	}
	
	/**
	 * Sends a message to all subscribers with the provided Serializable content.
	 * The content is processed before sending with an {@link EventProcessor} if such bean exists.
	 * User defined property IS_EVENT of the message is set to true.
	 */
	@Asynchronous
	public void fire(String eventName, Serializable content)
	{
		EventEntity event = eventRepo.find(eventName);
		
		assertEventExists(event, eventName);
		
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
		message.setPerformative(event.getName());
		message.setSender(localPlatformAid);
		message.getReceiverList().addAll(event.getRegisteredAgents());
		message.getUserDefinedParameters().put(EventMessageProperties.IS_EVENT, Boolean.toString(true));
		
		messageSender.send(message);
	}
	
	private EventProcessor findEventProcessor(String type)
	{
		try
		{
			return (EventProcessor)(new InitialContext()).lookup(ejbJNDIPrefix + type);
		}
		catch (NamingException | ClassCastException e)
		{
			return null;
		}
	}
	
	private void assertEventExists(EventEntity event, String eventName)
	{
		if(event == null)
		{
			log.error("Tried to fire nonexistent event: <{}>", eventName);
			throw new EventNotFound("Event <"+eventName+"> not found.");
		}
	}
}
