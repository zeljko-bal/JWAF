package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.template.reactive.AbstractReactiveAgent;
import org.jwaf.agent.template.reactive.annotation.DefaultMessageHandler;
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
		System.out.println("[TransportTestAgent] agent <"+aid.getName()+"> arrived at destination platform <"+localPlatformName+">.");
		System.out.println("aid param: test_param_key_1="+aid.getUserDefinedParameters().get("test_param_key_1"));
		System.out.println("aid param: test_param_key_2="+aid.getUserDefinedParameters().get("test_param_key_2"));
	}
}
