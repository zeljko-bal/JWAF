package org.jwaf.message.management;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jwaf.message.annotations.events.MessageSentEvent;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class MessageSender
{ 
	@Inject
	@MessageSentEvent
	Event<ACLMessage> messageEvent;
	
	public void send(ACLMessage message)
	{
		messageEvent.fire(message);
	}
}
