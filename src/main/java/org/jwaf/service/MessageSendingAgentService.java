package org.jwaf.service;

import java.io.Serializable;

import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.service.annotations.MessageSendingService;
import org.slf4j.Logger;

public abstract class MessageSendingAgentService implements AgentService
{
	protected AgentIdentifier caller;
	protected String serviceName;
	protected String performative;
	
	@Inject 
	protected MessageSender messageSender;
	
	@Inject
	private Logger log;
	
	public Object call(Object... params)
	{
		caller = new AgentIdentifier((String) params[0]);
		
		Object[] serviceParams = new Object[params.length-1];
		for(int i=1; i < params.length; i++)
		{
			serviceParams[i-1] = params[i];
		}
		
		if(!getClass().isAnnotationPresent(MessageSendingService.class))
		{
			log.error("MessageSendingAgentService {} should be annotated with @MessageSendingService.", getClass().getName());
			return callForMessage();
		}
		
		MessageSendingService annotation = getClass().getAnnotation(MessageSendingService.class);
		serviceName = annotation.name();
		performative = annotation.performative();
		
		Serializable result = callForMessage();
		
		ACLMessage message = new ACLMessage(performative, new AgentIdentifier(serviceName))
			.setContentAsObject(result);
		
		messageSender.send(message);
		
		return result;
	}
	
	protected abstract Serializable callForMessage(Object... params);
}
