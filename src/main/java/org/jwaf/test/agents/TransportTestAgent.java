package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.implementations.reactive.AbstractReactiveAgent;
import org.jwaf.agent.implementations.reactive.annotation.DefaultMessageHandler;
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
	public void onArrival()
	{
		log.info("Arrived at destination platform <{}>.", localPlatformName);
		log.info("aid params: test_param_key_1={}, test_param_key_2={}", 
				aid.getUserDefinedParameters().get("test_param_key_1"),
				aid.getUserDefinedParameters().get("test_param_key_2"));
	}
}
