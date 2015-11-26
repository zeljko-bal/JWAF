package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.base.implementations.reactive.AbstractReactiveAgent;
import org.jwaf.base.implementations.reactive.annotation.DefaultMessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class TransportTestAgent extends AbstractReactiveAgent
{
	@DefaultMessageHandler
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
