package org.jwaf.test.agents;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.base.implementations.behaviour.AbstractBehaviourAgent;
import org.jwaf.base.implementations.behaviour.annotations.InitialBehaviour;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.event.persistence.entity.TimerEventParam;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

@Stateless
@LocalBean
@TypeAttribute(key="key_1",value="val_1")
@InitialBehaviour("initial")
public class IntegrationTestAgent extends AbstractBehaviourAgent
{
	private static final String TEST_DATA = "TEST_DATA";
	
	@MessageHandler(behaviour="initial")
	public void initialState(ACLMessage newMessage)
	{
		log.info("activated, running initial tests..");
		
		printTestHeader("SETUP");
		
		data.map(TEST_DATA).put("error_count", "0");
		
		if(newMessage.getContentAsObject() instanceof TaskRequest)
		{
			TaskRequest taskRequest = (TaskRequest) newMessage.getContentAsObject();
			data.map(TEST_DATA).put("task_employer", taskRequest.getEmployer());
		}
		
		HashMap<String, String> params;
		List<String> typeResults;
		
		printTestHeader("aid tests");
		
		assertTrue(aid != null, "aid not null");
		assertEquals(aid.getAddresses().get(0), localPlatformAddress, "aid.getAddresses().get(0) == localPlatformAddress");
		
		printTestHeader("self tests");
		
		assertEquals(AgentState.ACTIVE, self.getState(), "self.getState() == ACTIVE");
		assertEquals(getClass().getSimpleName(), self.getType().getName(), "self.getType().getName() == getClass().getSimpleName()");
		assertEquals("val_1", self.getType().getAttributes().get("key_1"), "self.getType().getAttributes()");
		
		printTestHeader("data tests");
		
		data.map(TEST_DATA).put("private_key", "private_val");
		assertEquals("private_val", data.map(TEST_DATA).get("private_key"), "data.map(TEST_DATA).get('private_key')");
		
		data.getPublicDataMap().put("public_key", "public_val");
		assertEquals("public_val", data.getPublicDataMap().get("public_key"), "data.getPublicDataMap().get('public_key')");
		
		printTestHeader("agentDirectory tests");
		
		assertEquals(AgentState.ACTIVE, agentDirectory.getState(aid), "agentDirectory.getState(aid) == ACTIVE");
		assertEquals(AgentState.ACTIVE, agentDirectory.getState(aid.getName()), "agentDirectory.getState(aid.getName()) == ACTIVE");
		assertTrue(agentDirectory.localPlatformContains(aid), "agentDirectory.localPlatformContains(aid)");
		assertTrue(agentDirectory.localPlatformContains(aid.getName()), "agentDirectory.localPlatformContains(aid.getName())");
		assertEquals(agentDirectory.locationOf(aid), localPlatformName, "agentDirectory.locationOf(aid) == localPlatformName");
		assertEquals(agentDirectory.locationOf(aid.getName()), localPlatformName, "agentDirectory.locationOf(aid.getName()) == localPlatformName");
		
		AgentIdentifier aidResult =  agentDirectory.findAid(aid.getName());
		assertEquals(aid.getName(), aidResult.getName(), "agentDirectory.findAid(aid.getName())");
		
		assertEquals("public_val", agentDirectory.getPublicData(aid.getName()).get("public_key"), "agentDirectory.getPublicData(aid.getName())");
		
		printTestHeader("create pong agent");
		
		CreateAgentRequest createPongReq = new CreateAgentRequest("TestPongAgent");
		AgentIdentifier pongAid = agentDirectory.createAgent(createPongReq);
		
		assertTrue(agentDirectory.localPlatformContains(pongAid), "agent.localPlatformContains(pongAid)");
		assertEquals(AgentState.PASSIVE, agentDirectory.getState(pongAid), "agent.getState(pongAid) == PASSIVE");
		
		printTestHeader("type tests");
		
		assertEquals("IntegrationTestAgent", types.find("IntegrationTestAgent").getName(), "types.find('IntegrationTestAgent').getName()");
		assertEquals("val_1", types.find("IntegrationTestAgent").getAttributes().get("key_1"), "types.find('IntegrationTestAgent').getAttributes()");
		assertEquals("val_2", types.find("TestPongAgent").getAttributes().get("key_2"), "types.find('IntegrationTestAgent').getAttributes()");
		assertEquals("TestPongAgent", types.getTypeOf(pongAid).getName(), "types.getTypeOf(pongAid)");
		assertEquals("TestPongAgent", types.getTypeOf(pongAid.getName()).getName(), "types.getTypeOf(pongAid.getName())");
		
		printTestHeader("find type using parameter map");
		
		params = new HashMap<>();
		params.put("key_1", "val_1");
		typeResults = types.find(params)
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertTrue(typeResults.contains("IntegrationTestAgent"), "types.find(key_1) contains 'IntegrationTestAgent'");
		assertTrue(typeResults.contains("TestPongAgent"), "types.find(key_1) contains 'TestPongAgent'");
		
		params = new HashMap<>();
		params.put("key_2", "val_2");
		typeResults = types.find(params)
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertFalse(typeResults.contains("IntegrationTestAgent"), "types.find(key_2) not contains 'IntegrationTestAgent'");
		assertTrue(typeResults.contains("TestPongAgent"), "types.find(key_2) contains 'TestPongAgent'");
		
		params = new HashMap<>();
		params.put("key_1", "val_1");
		params.put("key_2", "val_2");
		typeResults = types.find(params)
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertFalse(typeResults.contains("IntegrationTestAgent"), "types.find(key_1 && key_2) not contains 'IntegrationTestAgent'");
		assertTrue(typeResults.contains("TestPongAgent"), "types.find(key_1 && key_2) contains 'TestPongAgent'");
		
		params = new HashMap<>();
		params.put("key_1", "val_1");
		params.put("key_2", "wrong_value");
		typeResults = types.find(params)
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertFalse(typeResults.contains("IntegrationTestAgent"), "types.find(key_1 && wrong_value) not contains 'IntegrationTestAgent'");
		assertFalse(typeResults.contains("TestPongAgent"), "types.find(key_1 && wrong_value) not contains 'TestPongAgent'");
		
		printTestHeader("find type using query");
		
		typeResults = types.find(q->q.field("attributes.key_1").equal("val_1"))
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertTrue(typeResults.contains("IntegrationTestAgent"), "types.find(key_1) contains 'IntegrationTestAgent'");
		assertTrue(typeResults.contains("TestPongAgent"), "types.find(key_1) contains 'TestPongAgent'");
		
		typeResults = types.find(q->q.field("attributes.key_2").equal("val_2"))
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertFalse(typeResults.contains("IntegrationTestAgent"), "types.find(key_1) contains 'IntegrationTestAgent'");
		assertTrue(typeResults.contains("TestPongAgent"), "types.find(key_1) contains 'TestPongAgent'");
		
		typeResults = types.find(q->
				{
					q.or(q.criteria("attributes.key_1").equal("val_1"), 
						 q.criteria("attributes.key_2").equal("val_2"));
					return q;
				})
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertTrue(typeResults.contains("IntegrationTestAgent"), "types.find(key_1 || key_2) contains 'IntegrationTestAgent'");
		assertTrue(typeResults.contains("TestPongAgent"), "types.find(key_1 || key_2) contains 'TestPongAgent'");
		
		typeResults = types.find(q->q.field("attributes.key_1").equal("val_1")
									 .field("attributes.key_2").equal("val_2"))
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertFalse(typeResults.contains("IntegrationTestAgent"), "types.find(key_1 && key_2) contains 'IntegrationTestAgent'");
		assertTrue(typeResults.contains("TestPongAgent"), "types.find(key_1 && key_2) contains 'TestPongAgent'");
		
		typeResults = types.find(q->q.field("attributes.key_1").equal("val_1")
				 					 .field("attributes.key_2").equal("wrong_value"))
				.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
		assertFalse(typeResults.contains("IntegrationTestAgent"), "types.find(key_1 && wrong_value) contains 'IntegrationTestAgent'");
		assertFalse(typeResults.contains("TestPongAgent"), "types.find(key_1 && wrong_value) contains 'TestPongAgent'");
		
		printTestHeader("awaiting reply from pong agent");
		behaviours.changeTo("expecting_pong");
		
		printTestHeader("sending ping to pong agent");
		log.info("pinging pong agent <{}>.", pongAid.getName());
		messages.send(new ACLMessage().setPerformative("test_ping").addReceivers(pongAid));
	}
	
	@MessageHandler(behaviour="expecting_pong")
	public void expectingPong(ACLMessage newMessage) throws InterruptedException, ExecutionException
	{
		HashMap<String, String> params;
		List<String> serviceResults;
		
		log.info("Got pong from <{}>. Proceeding with tests..", newMessage.getSender().getName());
		
		printTestHeader("testing data between calls");
		
		assertEquals("private_val", data.map(TEST_DATA).get("private_key"), "data.map(TEST_DATA).get('private_key') second time");
		assertEquals("public_val", data.getPublicDataMap().get("public_key"), "data.getPublicDataMap().get('public_key') second time");
		
		printTestHeader("newMessage tests");
		
		assertEquals("test_pong", newMessage.getPerformative(), "newMessage.getPerformative() = test_pong");
		
		assertTrue(agentDirectory.localPlatformContains(newMessage.getSender()), "agent.localPlatformContains(newMessage.getSender())");
		
		assertEquals("TestPongAgent", types.getTypeOf(newMessage.getSender()).getName(), "type.getTypeOf(newMessage.getSender())");
		assertEquals(AgentState.PASSIVE, agentDirectory.getState(newMessage.getSender()), "agent.getState(newMessage.getSender())");
		
		printTestHeader("service tests");
		
		assertTrue(services.exists("TestAgentService"), "service.exists('TestAgentService')");
		assertEquals("val_1", services.getAttributes("TestAgentService").get("key_1"), "service.getAttributes('TestAgentService')");
		assertEquals("params=123", services.callSynch("TestAgentService", "1", "2", "3"), "service.callSynch('TestAgentService', '1', '2', '3')");
		assertEquals("params=123", services.callAsynch("TestAgentService", "1", "2", "3").get(), "service.callAsynch('TestAgentService', '1', '2', '3')");
		
		params = new HashMap<>();
		params.put("key_1", "val_1");
		serviceResults = services.find(params);
		assertTrue(serviceResults.contains("TestAgentService"), "service.find(key_1)");
		
		params = new HashMap<>();
		params.put("key_1", "wrong_value");
		serviceResults = services.find(params);
		assertFalse(serviceResults.contains("TestAgentService"), "service.find(wrong_value)");
		
		serviceResults = services.find(q->q.field("attributes.key_1").equal("val_1"));
		assertTrue(serviceResults.contains("TestAgentService"), "service.find(key_1) query");
		
		serviceResults = services.find(q->q.field("attributes.key_1").equal("wrong_value"));
		assertFalse(serviceResults.contains("TestAgentService"), "service.find(wrong_value) query");
		
		printTestHeader("event tests");
		
		events.register(testEventName());
		assertTrue(events.exists(testEventName()), "event.exists");
		
		printTestHeader("request of pong agent to subscribe to integration_test_evt");
		
		messages.send(new ACLMessage()
				.setPerformative("subscribe_request")
				.addReceivers(newMessage.getSender())
				.setContent(testEventName()));
		
		behaviours.changeTo("expecting_subscribed_pong");
	}
	
	@MessageHandler(behaviour="expecting_subscribed_pong")
	public void expectingSubscribedPong(ACLMessage newMessage)
	{
		log.info("Got subscribed pong from <{}>. Proceeding with tests..", newMessage.getSender().getName());
		
		printTestHeader("newMessage tests");
		assertEquals("subscribed_pong", newMessage.getPerformative(), "newMessage.getPerformative() = subscribed_pong");
		
		printTestHeader("fireing event");
		events.fire(testEventName(), "event_ping");
		
		behaviours.changeTo("expecting_event_pong");
	}
	
	@MessageHandler(behaviour="expecting_event_pong")
	public void expectingEventPong(ACLMessage newMessage)
	{
		log.info("Got event pong from <{}>. Proceeding with tests..", newMessage.getSender().getName());
		
		printTestHeader("newMessage tests");
		assertEquals("event_pong", newMessage.getPerformative(), "newMessage.getPerformative() = event_pong");
		assertEquals("event_ping", newMessage.getContent(), "newMessage.getContent() = event_ping");
		
		printTestHeader("registring timer");
		timers.register(testTimerName(), testEventName(), (new ScheduleExpression()).hour("*").minute("*").second("*/1"));
		
		behaviours.changeTo("expecting_timer_pong");
	}
	
	@MessageHandler(behaviour="expecting_timer_pong")
	public void expectingTimerPong(ACLMessage newMessage) throws InterruptedException
	{
		log.info("Got timer pong from <{}>. Proceeding with tests..", newMessage.getSender().getName());
		
		printTestHeader("newMessage tests");
		assertEquals("event_pong", newMessage.getPerformative(), "newMessage.getPerformative() = event_pong in expecting_timer_pong");
		TimerEventParam eventParam = (TimerEventParam) newMessage.getContentAsObject();
		assertTrue(eventParam!=null, "eventParam!=null");
		assertEquals(testTimerName(), eventParam.getTimerName(), "eventParam.getTimerName()");
		
		printTestHeader("waiting for 3s");
		Thread.sleep(3000);
		
		printTestHeader("message tests");
		assertTrue(messages.newMessagesAvailable(), "messages.newMessagesAvailable()");
		assertEquals("event_pong", messages.getAll().get(0).getPerformative(), "newMessage.getPerformative() = event_pong in expecting_timer_pong");
		
		printTestHeader("unregistring timer");
		timers.unregister(testTimerName());
		
		printTestHeader("waiting 1s for messages to arrive");
		Thread.sleep(1000);
		
		printTestHeader("message tests");
		messages.ignoreAndForgetNewMessages();
		assertFalse(messages.newMessagesAvailable(), "ignoreAndForgetNewMessages");
		
		printTestHeader("unregistring event");
		events.unregister(testEventName());
		assertFalse(events.exists("integration_test_evt"), "event.unregister");
		
		printTestHeader("sending termination request");
		agentDirectory.requestAgentTermination(newMessage.getSender().getName());
		
		printTestHeader("waiting 2s for agent to terminate");
		Thread.sleep(2000);
		
		assertFalse(agentDirectory.localPlatformContains(newMessage.getSender()), "agent deleted");
		
		String errorCount = data.map(TEST_DATA).get("error_count");
		
		data.map(TEST_DATA).append("report", 
				"=============================\n	tests complete. errorCount = "+errorCount+"\n");
		
		String report = data.map(TEST_DATA).get("report");
		
		if(data.map(TEST_DATA).containsKey("task_employer"))
		{
			String employer = data.map(TEST_DATA).get("task_employer");
			tasks.submitResult(new TaskResult(employer, "IntegrationTestTask", report));
		}
		
		log.info("\n\n"+report);
		
		self.terminate();
	}
	
	private void assertEquals(Object o1, Object o2, String message)
	{
		if(o1 == null)
		{
			data.map(TEST_DATA).append("report", "[Test Failed] "+ message +" ; First parameter is null.\n");
			incrementErrorCount();
			return;
		}
		
		assertTrue(o1.equals(o2), message+" ; " + "<"+o1+"> and <"+o2+"> should be equal.");
	}

	private void assertTrue(boolean exp, String message)
	{
		if(!exp)
		{
			data.map(TEST_DATA).append("report", "[Test Failed] "+message+"\n")	;
			incrementErrorCount();
		}
	}
	
	private void assertFalse(boolean exp, String message)
	{
		assertTrue(!exp, message);
	}
	
	private void incrementErrorCount()
	{
		data.map(TEST_DATA).increment("error_count");
	}
	
	private void printTestHeader(String message)
	{
		log.info("--------"+message+"--------");
	}
	
	private String testTimerName()
	{
		return "test_tmr_"+aid.getName();
	}
	
	private String testEventName()
	{
		return "test_evt_"+aid.getName();
	}
}
