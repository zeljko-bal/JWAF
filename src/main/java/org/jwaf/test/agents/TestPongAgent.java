package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.base.implementations.behaviour.AbstractBehaviourAgent;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.message.performative.PlatformPerformative;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
@TypeAttribute(key="test_attr_key_2",value="test_attr_value_2")
public class TestPongAgent extends AbstractBehaviourAgent
{
	private static final String TEST_DATA = "TEST_DATA";
	
	@MessageHandler(performative="test_ping")
	public void handlePing(ACLMessage newMessage)
	{
		log.info("Got ping from: <{}>.", newMessage.getSender().getName());
		
		messages.send(new ACLMessage().setPerformative("test_pong").addReceivers(newMessage.getSender()));
	}
	
	@MessageHandler(performative="subscribe_request")
	public void handleSubscribeRequest(ACLMessage newMessage)
	{
		log.info("Got subscribe request for event: <{}>.", newMessage.getContent());
		events.subscribe(newMessage.getContent());
		data.map(TEST_DATA).put("ping_aid", newMessage.getSender().getName());
		messages.send(new ACLMessage().setPerformative("subscribed_pong").addReceivers(newMessage.getSender()));
	}
	
	@MessageHandler
	public void defaultHandler(ACLMessage newMessage)
	{
		if("integration_test_evt".equals(newMessage.getPerformative()))
		{
			log.info("Got integration_test_evt event notification.");
			AgentIdentifier pingAid = agentDirectory.findAid(data.map(TEST_DATA).get("ping_aid"));
			messages.send(new ACLMessage().setPerformative("event_pong").addReceivers(pingAid).setContent(newMessage.getContent()));
		}
		else
		{
			log.info("Got message with unknown performative: <{}>.", newMessage.getPerformative());
		}
	}
	
	@MessageHandler(performative=PlatformPerformative.SELF_TERMINATE)
	public void terminate(ACLMessage newMessage)
	{
		self.terminate();
	}
}
