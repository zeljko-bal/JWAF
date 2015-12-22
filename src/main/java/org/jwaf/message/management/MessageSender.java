package org.jwaf.message.management;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jwaf.message.annotations.events.MessageSentEvent;
import org.jwaf.message.persistence.entity.ACLMessage;

/**
 * A helper bean that notifies the {@link MessageManager} about a message that has ben sent from this platform.
 * 
 * @author zeljko.bal
 */
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
