package org.jwaf.test.agents;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.base.implementations.behaviour.AbstractBehaviourAgent;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.message.performative.PlatformPerformative;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class PongTestAgent extends AbstractBehaviourAgent
{
	@MessageHandler(performative="ping")
	public void ping(ACLMessage message)
	{
		log.info("got ping form agent <{}>", message.getSender().getName());
		
		messages.send(createPong(message)
				.setContent(message.getContent())
				.setPerformative("pong"));
	}
	
	@MessageHandler(performative="object_ping")
	public void contentPing(ACLMessage message)
	{
		log.info("got object_ping form agent <{}>", message.getSender().getName());
		
		TestComplexObject pingObject = (TestComplexObject) message.getContentAsObject();
		TestComplexObject pongObject = new TestComplexObject(pingObject);
		
		messages.send(createPong(message)
				.setContentAsObject(pongObject)
				.setPerformative("object_pong"));
	}
	
	@MessageHandler(performative=PlatformPerformative.SELF_TERMINATE)
	public void terminate(ACLMessage newMessage)
	{
		self.terminate();
	}
	
	private ACLMessage createPong(ACLMessage message)
	{
		ACLMessage pong = new ACLMessage()
				.addReceivers(message.getSender())
				.setConversation_id(message.getConversation_id())
				.setEncoding(message.getEncoding())
				.setLanguage(message.getLanguage())
				.setOntology(message.getOntology())
				.setProtocol(message.getProtocol())
				.setReply_by(message.getReply_by())
				.setReply_to(message.getReply_to())
				.setReply_with(message.getReply_with());
		
		pong.getUserDefinedParameters().putAll(message.getUserDefinedParameters());
		
		return pong;
	}
	
	public static class TestComplexObject implements Serializable
	{
		private static final long	serialVersionUID	= 1312800899954230364L;
		
		public String stringField;
		public int intField;
		public Date dateField;
		
		public TestComplexObject(TestComplexObject other)
		{
			this.stringField = other.stringField;
			this.intField = other.intField;
			this.dateField = other.dateField;
		}

		public TestComplexObject(String stringField, int intField, Date dateField)
		{
			this.stringField = stringField;
			this.intField = intField;
			this.dateField = dateField;
		}
	}
}
