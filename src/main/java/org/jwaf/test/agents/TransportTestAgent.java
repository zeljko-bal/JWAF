package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.base.implementations.behaviour.AbstractBehaviourAgent;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class TransportTestAgent extends AbstractBehaviourAgent
{
	@MessageHandler
	public void travel(ACLMessage newMessage)
	{
		remotePlatforms.travelTo(newMessage.getContent());
	}
	
	@Override
	public void onArrival(String data)
	{
		log.info("Arrived at destination platform <{}>.", localPlatformName);
	}
}
