package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentDataType;
import org.jwaf.agent.template.reactive.AbstractReactiveAgent;
import org.jwaf.agent.template.reactive.annotation.DefaultMessageHandler;
import org.jwaf.agent.template.reactive.annotation.MessageHandler;
import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.jwaf.message.performative.PlatformPerformative;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
@TypeAttributes(@TypeAttribute(key="test_attr_key_2",value="test_attr_value_2"))
public class TestPongAgent extends AbstractReactiveAgent
{
	@MessageHandler("test_ping")
	public void handlePing(ACLMessage newMessage)
	{
		System.out.println("[TestPongAgent] Got ping from: <"+newMessage.getSender().getName()+">.");
		
		message.send(new ACLMessage().setPerformative("test_pong").addReceivers(newMessage.getSender()));
	}
	
	@MessageHandler("subscribe_request")
	public void handleSubscribeRequest(ACLMessage newMessage)
	{
		System.out.println("[TestPongAgent] Got subscribe request for event: <"+newMessage.getContent()+">");
		event.subscribe(newMessage.getContent());
		self.getData(AgentDataType.PRIVATE).put("ping_aid", newMessage.getSender().getName());
		message.send(new ACLMessage().setPerformative("subscribed_pong").addReceivers(newMessage.getSender()));
	}
	
	@DefaultMessageHandler
	public void defaultHandler(ACLMessage newMessage)
	{
		if("integration_test_evt".equals(newMessage.getPerformative()))
		{
			System.out.println("[TestPongAgent] Got integration_test_evt event notification.");
			AgentIdentifier pingAid = agent.findAid(self.getData(AgentDataType.PRIVATE).get("ping_aid"));
			message.send(new ACLMessage().setPerformative("event_pong").addReceivers(pingAid).setContent(newMessage.getContent()));
		}
		else
		{
			System.out.println("[TestPongAgent] Got message with unknown performative: <"+newMessage.getPerformative()+">");
		}
	}
	
	@MessageHandler(PlatformPerformative.SELF_TERMINATE)
	public void terminate(ACLMessage newMessage)
	{
		self.terminate();
	}
}
