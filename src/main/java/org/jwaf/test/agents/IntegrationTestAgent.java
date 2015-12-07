package org.jwaf.test.agents;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.base.implementations.fsm.AbstractFSMAgent;
import org.jwaf.base.implementations.fsm.annotation.StateCallback;
import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.event.persistence.entity.TimerEventParam;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

@Stateless
@LocalBean
@TypeAttribute(key="test_attr_key_1",value="test_attr_value_1")
public class IntegrationTestAgent extends AbstractFSMAgent
{
	private static final String TEST_DATA = "TEST_DATA";
	
	@StateCallback(state="initial_state", initial=true)
	public void initialState(ACLMessage newMessage)
	{
		log.info("activated, running initial tests..");
		
		data.map(TEST_DATA).put("error_count", "0");
		
		if(newMessage.getContentAsObject() instanceof TaskRequest)
		{
			TaskRequest taskRequest = (TaskRequest) newMessage.getContentAsObject();
			data.map(TEST_DATA).put("task_employer", taskRequest.getEmployer());
		}
		
		HashMap<String, String> params;
		
		// aid
		assertTrue(aid != null, "aid not null");
		
		// self
		assertEquals(AgentState.ACTIVE, self.getState(), "self.getState()");
		assertEquals("IntegrationTestAgent", self.getType().getName(), "self.getType().getName()");
		assertEquals("test_attr_value_1", self.getType().getAttributes().get("test_attr_key_1"), "self.getType().getAttributes()");
		
		data.map(TEST_DATA).put("test_private_data_key", "test_private_data_val");
		assertEquals("test_private_data_val", data.map(TEST_DATA).get("test_private_data_key"), "self.getData(AgentDataType.PRIVATE)");
		
		data.getPublicDataMap().put("test_public_data_key", "test_public_data_val");
		assertEquals("test_public_data_val", data.getPublicDataMap().get("test_public_data_key"), "self.getData(AgentDataType.PUBLIC)");
		
		// agent
		assertEquals(AgentState.ACTIVE, agent.getState(aid.getName()), "agent.getState(aid.getName())");
		assertTrue(agent.localPlatformContains(aid), "agent.localPlatformContains(aid)");
		assertEquals(agent.locationOf(aid), localPlatformName, "agent.locationOf(aid)");
		
		AgentIdentifier aidResult =  agent.findAid(aid.getName());
		assertEquals(aid.getName(), aidResult.getName(), "agent.findAid(aid.getName().getName()");
		
		assertEquals("test_public_data_val", agent.getPublicData(aid.getName()).get("test_public_data_key"), "agent.getPublicData(aid.getName())");
		
		// create pong agent
		CreateAgentRequest createPongReq = new CreateAgentRequest("TestPongAgent");
		createPongReq.getParams().put("test_pong_param_key_1", "test_pong_param_val_1");
		AgentIdentifier pongAid = agent.createAgent(createPongReq);
		
		assertTrue(agent.localPlatformContains(pongAid), "agent.localPlatformContains(pongAid)");
		assertEquals(AgentState.PASSIVE, agent.getState(pongAid), "agent.getState(pongAid)");
		
		// type
		assertEquals("IntegrationTestAgent", type.find("IntegrationTestAgent").getName(), "type.find('IntegrationTestAgent').getName()");
		assertEquals("test_attr_value_1", type.find("IntegrationTestAgent").getAttributes().get("test_attr_key_1"), "type.find('IntegrationTestAgent').getAttributes()");
		assertEquals("test_attr_value_2", type.find("TestPongAgent").getAttributes().get("test_attr_key_2"), "type.find('IntegrationTestAgent').getAttributes()");
		params = new HashMap<>();
		params.put("test_attr_key_1", "test_attr_value_1");
		assertEquals("IntegrationTestAgent", type.find(params).get(0).getName(), "type.find(params)");
		assertEquals("TestPongAgent", type.getTypeOf(pongAid).getName(), "type.getTypeOf(pongAid)");
		
		// send ping
		log.info("pinging pong agent <{}>.", pongAid.getName());
		message.send(new ACLMessage().setPerformative("test_ping").addReceivers(pongAid));
		
		// await reply
		stateHandling.changeState("expecting_pong");
	}
	
	@StateCallback(state="expecting_pong")
	public void expectingPong(ACLMessage newMessage) throws InterruptedException, ExecutionException
	{
		HashMap<String, String> params;
		
		log.info("Got pong from <{}>. Proceeding with tests..", newMessage.getSender().getName());
		
		// data between calls
		assertEquals("test_private_data_val", data.map(TEST_DATA).get("test_private_data_key"), "self.getData(AgentDataType.PRIVATE) second time");
		assertEquals("test_public_data_val", data.getPublicDataMap().get("test_public_data_key"), "self.getData(AgentDataType.PUBLIC) second time");
		
		// newMessage tests
		assertEquals("test_pong", newMessage.getPerformative(), "newMessage.getPerformative() = test_pong");
		
		assertTrue(agent.localPlatformContains(newMessage.getSender()), "agent.localPlatformContains(newMessage.getSender())");
		
		assertEquals("TestPongAgent", type.getTypeOf(newMessage.getSender()).getName(), "type.getTypeOf(newMessage.getSender())");
		assertEquals(AgentState.PASSIVE, agent.getState(newMessage.getSender()), "agent.getState(newMessage.getSender())");
		
		// service
		assertTrue(service.exists("TestAgentService"), "service.exists('TestAgentService')");
		assertEquals("test_service_attr_val_1", service.getAttributes("TestAgentService").get("test_service_attr_key_1"), "service.getAttributes('TestAgentService')");
		assertEquals("params=123", service.callSynch("TestAgentService", "1", "2", "3"), "service.callSynch('TestAgentService', '1', '2', '3')");
		assertEquals("params=123", service.callAsynch("TestAgentService", "1", "2", "3").get(), "service.callAsynch('TestAgentService', '1', '2', '3')");
		params = new HashMap<>();
		params.put("test_service_attr_key_1", "test_service_attr_val_1");
		assertEquals("TestAgentService", service.find(params).get(0), "service.find(params).get(0)");
		
		// event
		event.register("integration_test_evt");
		assertTrue(event.exists("integration_test_evt"), "event.exists");
		
		// request of pong agent to subscribe to integration_test_evt
		message.send(new ACLMessage().setPerformative("subscribe_request").addReceivers(newMessage.getSender()).setContent("integration_test_evt"));
		
		stateHandling.changeState("expecting_subscribed_pong");
	}
	
	@StateCallback(state="expecting_subscribed_pong")
	public void expectingSubscribedPong(ACLMessage newMessage)
	{
		log.info("Got subscribed pong from <{}>. Proceeding with tests..", newMessage.getSender().getName());
		
		// newMessage tests
		assertEquals("subscribed_pong", newMessage.getPerformative(), "newMessage.getPerformative() = subscribed_pong");
		
		// fire event
		event.fire("integration_test_evt", "event_ping");
		
		stateHandling.changeState("expecting_event_pong");
	}
	
	@StateCallback(state="expecting_event_pong")
	public void expectingEventPong(ACLMessage newMessage)
	{
		log.info("Got event pong from <{}>. Proceeding with tests..", newMessage.getSender().getName());
		
		// newMessage tests
		assertEquals("event_pong", newMessage.getPerformative(), "newMessage.getPerformative() = event_pong");
		assertEquals("event_ping", newMessage.getContent(), "newMessage.getContent() = event_ping");
		
		// register timer
		timer.register("integration_test_timer", "integration_test_evt", (new ScheduleExpression()).hour("*").minute("*").second("*/1"));
		
		stateHandling.changeState("expecting_timer_pong");
	}
	
	@StateCallback(state="expecting_timer_pong")
	public void expectingTimerPong(ACLMessage newMessage) throws InterruptedException
	{
		log.info("Got timer pong from <{}>. Proceeding with tests..", newMessage.getSender().getName());
		
		// newMessage tests
		assertEquals("event_pong", newMessage.getPerformative(), "newMessage.getPerformative() = event_pong in expecting_timer_pong");
		TimerEventParam eventParam = (TimerEventParam) newMessage.getContentAsObject();
		assertTrue(eventParam!=null, "eventParam!=null");
		assertEquals("integration_test_timer", eventParam.getTimerName(), "eventParam.getTimerName()");
		
		// wait for 3s
		Thread.sleep(3000);
		
		// message
		assertTrue(message.newMessagesAvailable(), "message.newMessagesAvailable()");
		assertEquals("event_pong", message.getAll().get(0).getPerformative(), "newMessage.getPerformative() = event_pong in expecting_timer_pong");
		
		// unregister timer
		timer.unregister("integration_test_timer");
		
		// wait for messages to arrive
		Thread.sleep(1000);
		
		// message
		message.ignoreAndForgetNewMessages();
		assertTrue(!message.newMessagesAvailable(), "ignoreAndForgetNewMessages");
		
		// event unregister
		event.unregister("integration_test_evt");
		assertTrue(!event.exists("integration_test_evt"), "event.unregister");
		
		// send termination request
		agent.requestAgentTermination(newMessage.getSender().getName());
		
		// wait for agent to terminate
		Thread.sleep(5000);
		
		assertTrue(!agent.localPlatformContains(newMessage.getSender()), "agent deleted");
		
		String errorCount = data.map(TEST_DATA).get("error_count");
		
		data.map(TEST_DATA).append("report", "[IntegrationTestAgent] tests complete. errorCount = "+errorCount+"\n");
		
		if(data.map(TEST_DATA).containsKey("task_employer"))
		{
			String employer = data.map(TEST_DATA).get("task_employer");
			String report = data.map(TEST_DATA).get("report");
			task.submitResult(new TaskResult(employer, "IntegrationTestTask", report));
		}
		
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
	
	private void incrementErrorCount()
	{
		data.map(TEST_DATA).increment("error_count");
	}
}
