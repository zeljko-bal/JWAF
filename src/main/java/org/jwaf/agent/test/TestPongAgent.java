package org.jwaf.agent.test;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.template.reactive.AbstractReactiveAgent;
import org.jwaf.agent.template.reactive.annotation.MessageHandler;
import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
@TypeAttributes(@TypeAttribute(key="test_attr_key_2",value="test_attr_value_2"))
public class TestPongAgent extends AbstractReactiveAgent
{
	@MessageHandler("test-ping")
	public void handlePing(ACLMessage newMessage)
	{
		System.out.println("[TestPongAgent] Got ping from: <"+newMessage.getSender().getName()+">.");
		
		message.send(new ACLMessage().setPerformative("test-pong").addReceivers(newMessage.getSender()));
	}
}
