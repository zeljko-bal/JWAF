package org.jwaf.test.agents;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.test.TestData;
import org.jwaf.test.agents.PongTestAgent.TestComplexObject;

@Stateless
@LocalBean
public class MessageTestAgent extends AbstractTestAgent
{
	private static final String	conversation_id = "test_id";
	private static final String	encoding = "encoding";
	private static final String	language = "language";
	private static final String	ontology = "ontology";
	private static final String	protocol = "protocol";
	private static final Date reply_by = new Date(42);
	private static final String	reply_to = "reply_to";
	private static final String	reply_with = "reply_with";
	private static final String	ping_content = "ping_content";
	private static final TestComplexObject object_ping_content	= 
			new TestComplexObject("string_field", 42, new Date(42));
	private static final String	TEST_DATA = "MY_TEST_DATA";
	
	@MessageHandler
	public void test(ACLMessage message)
	{
		String helperTypeName = PongTestAgent.class.getSimpleName();
		AgentIdentifier helperAid = agentDirectory.createAgent(new CreateAgentRequest(helperTypeName));
		
		data.map(TEST_DATA).put("hrlper", helperAid.getName());
		data.map(TEST_DATA).put("employer", message.getSender().getName());
		
		log.info("sending ping to helper agent <{}>", helperAid.getName());
		
		messages.send(createPing(helperAid)
				.setPerformative("ping")
				.setContent(ping_content));
		
		behaviours.changeTo("expecting_pong");
	}
	
	@MessageHandler(behaviour="expecting_pong")
	public void expectingPong(ACLMessage message)
	{
		String helper = data.map(TEST_DATA).get("hrlper");
		
		log.info("got pong form helper agent <{}>", helper);
		
		doMessageTests(message, helper);
		
		t.assertEqual(message.getPerformative(), "pong", "message.getPerformative()");
		t.assertEqual(message.getContent(), ping_content, "message.getContent()");
		
		log.info("sending object_ping to helper agent");
		
		messages.send(createPing(new AgentIdentifier(helper))
				.setPerformative("object_ping")
				.setContentAsObject(object_ping_content));
		
		behaviours.changeTo("expecting_object_pong");
	}
	
	@MessageHandler(behaviour="expecting_object_pong")
	public void expectingObjectPong(ACLMessage message)
	{
		String helper = data.map(TEST_DATA).get("hrlper");
		
		log.info("got object_pong form helper agent <{}>", helper);
		
		agentDirectory.requestAgentTermination(helper);
		
		doMessageTests(message, helper);
		
		t.assertEqual(message.getPerformative(), "object_pong", "message.getPerformative()");
		
		TestComplexObject pongObject = (TestComplexObject) message.getContentAsObject();
		
		t.assertEqual(pongObject.stringField, object_ping_content.stringField, "pongObject.stringField");
		t.assertEqual(pongObject.intField, object_ping_content.intField, "pongObject.intField");
		t.assertEqual(pongObject.dateField, object_ping_content.dateField, "pongObject.dateField");
		
		String helperTypeName = MessageQueryTestHelper.class.getSimpleName();
		AgentIdentifier helperAid = agentDirectory.createAgent(new CreateAgentRequest(helperTypeName));
		
		messages.send(new ACLMessage().setPerformative("perf1").addReceivers(helperAid));
		messages.send(new ACLMessage().setPerformative("perf2").addReceivers(helperAid));
		messages.send(new ACLMessage().setPerformative("perf1").addReceivers(helperAid));
		
		behaviours.changeTo("expecting_message_query");
	}
	
	@MessageHandler(behaviour="expecting_message_query")
	public void expectingMessageQuery(ACLMessage message)
	{
		TestData testData = (TestData) message.getContentAsObject();
		t.appendErrors(testData.getErrors());
		
		String employer = data.map(TEST_DATA).get("employer");
		t.sendResults(new AgentIdentifier(employer));
		self.terminate();
	}
	
	private void doMessageTests(ACLMessage message, String helper)
	{
		t.assertEqual(message.getSender().getName(), helper, "message.getSender().getName()");
		t.assertEqual(message.getConversation_id(), conversation_id, "message.getConversation_id()");
		t.assertEqual(message.getEncoding(), encoding, "message.getEncoding()");
		t.assertEqual(message.getLanguage(), language, "message.getLanguage()");
		t.assertEqual(message.getOntology(), ontology, "message.getOntology()");
		t.assertEqual(message.getProtocol(), protocol, "message.getProtocol()");
		t.assertEqual(message.getReply_by(), reply_by, "message.getReply_by()");
		t.assertEqual(message.getReply_to(), reply_to, "message.getReply_to()");
		t.assertEqual(message.getReply_with(), reply_with, "message.getReply_with()");
		t.assertEqual(message.getUserDefinedParameters().get("key_1"), "val_1", "message.getUserDefinedParameters().get(key_1)");
		t.assertEqual(message.getUserDefinedParameters().get("key_2"), "val_2", "message.getUserDefinedParameters().get(key_2)");
	}
	
	private ACLMessage createPing(AgentIdentifier to)
	{
		ACLMessage ping = new ACLMessage()
				.addReceivers(to)
				.setConversation_id(conversation_id)
				.setEncoding(encoding)
				.setLanguage(language)
				.setOntology(ontology)
				.setProtocol(protocol)
				.setReply_by(reply_by)
				.setReply_to(reply_to)
				.setReply_with(reply_with);
		
		ping.getUserDefinedParameters().put("key_1", "val_1");
		ping.getUserDefinedParameters().put("key_2", "val_2");
		
		return ping;
	}
}
