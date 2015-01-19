package org.jwaf.agent.test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.agent.persistence.repository.AgentDataType;
import org.jwaf.agent.template.fsm.AbstractFSMAgent;
import org.jwaf.agent.template.fsm.annotation.StateCallback;
import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
@TypeAttributes(@TypeAttribute(key="test_attr_key_1",value="test_attr_value_1"))
public class IntegrationTestAgent extends AbstractFSMAgent
{
	private int errorCount = 0;
	
	@StateCallback(state="initial-tests", initial=true)
	public void initialState(ACLMessage newMessage)
	{
		System.out.println("[IntegrationTestAgent] activated, name="+aid.getName()+", running initial tests..");
		
		HashMap<String, String> params;
		
		// aid
		assertTrue(aid != null, "aid not null");
		
		// self
		assertEquals(AgentState.ACTIVE, self.getState(), "self.getState()");
		assertEquals("IntegrationTestAgent", self.getType().getName(), "self.getType().getName()");
		assertEquals("test_attr_value_1", self.getType().getAttributes().get("test_attr_key_1"), "self.getType().getAttributes()");
		
		self.getData(AgentDataType.PRIVATE).put("test_private_data_key", "test_private_data_val");
		assertEquals("test_private_data_val", self.getData(AgentDataType.PRIVATE).get("test_private_data_key"), "self.getData(AgentDataType.PRIVATE)");
		
		self.getData(AgentDataType.PUBLIC).put("test_public_data_key", "test_public_data_val");
		assertEquals("test_public_data_val", self.getData(AgentDataType.PUBLIC).get("test_public_data_key"), "self.getData(AgentDataType.PUBLIC)");
		
		// aid
		assertEquals("test_param_value_1", aid.getUserDefinedParameters().get("test_param_key_1"), "aid.getUserDefinedParameters parameter");
		
		// agent
		assertEquals(AgentState.ACTIVE, agent.getState(aid.getName()), "agent.getState(aid.getName())");
		assertTrue(agent.localPlatformContains(aid), "agent.localPlatformContains(aid)");
		assertEquals(agent.locationOf(aid), "jwaf1", "agent.locationOf(aid)"); // TODO get local platform aid
		
		AgentIdentifier aidResult =  agent.findAid(aid.getName());
		assertEquals(aid.getName(), aidResult.getName(), "agent.findAid(aid.getName().getName()");
		assertEquals("test_param_value_1", aidResult.getUserDefinedParameters().get("test_param_key_1"), "agent.findAid(aid.getName().getUserDefinedParameters()");
		assertEquals("test_public_data_val", agent.getPublicData(aid.getName()).get("test_public_data_key"), "agent.getPublicData(aid.getName())");
		
		params = new HashMap<>();
		params.put("test_param_key_1", "test_param_value_1");
		assertEquals(aid.getName(), agent.findAid(params).get(0).getName(), "agent.findAid(params) 1");
		
		params.put("nonexistent_parameter", "nonexistent_parameter");
		assertTrue(agent.findAid(params).isEmpty(), "agent.findAid(params) nonexistent");
		
		params = new HashMap<>();
		params.put("test_param_key_1", "test_param_value_1");
		params.put("test_param_key_2", "test_param_value_2");
		assertEquals(aid.getName(), agent.findAid(params).get(0).getName(), "agent.findAid(params) 1 and 2");
		
		params = new HashMap<>();
		params.put("test_param_key_1", "test_param_value_1");
		params.put("test_param_key_2", "wrong_value");
		assertTrue(agent.findAid(params).isEmpty(), "agent.findAid(params) wrong value");
		
		params = new HashMap<>();
		params.put("test_param_key_1", "test_param_value_1");
		params.put("wrong_key", "test_param_value_2");
		assertTrue(agent.findAid(params).isEmpty(), "agent.findAid(params) wrong key");
		
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
		System.out.println("[IntegrationTestAgent] pinging pong agent <"+pongAid.getName()+">.");
		message.send(new ACLMessage().setPerformative("test-ping").addReceivers(pongAid));
		
		// await reply
		stateHandling.changeState("expecting-pong");
	}
	
	@StateCallback(state="expecting-pong")
	public void expectingPong(ACLMessage newMessage) throws InterruptedException, ExecutionException
	{
		HashMap<String, String> params;
		
		System.out.println("[IntegrationTestAgent] Got pong from <"+newMessage.getSender().getName()+">. Proceeding with tests..");
		
		// newMessage tests
		assertEquals("test-pong", newMessage.getPerformative(), "newMessage.getPerformative() = pong");
		
		assertTrue(agent.localPlatformContains(newMessage.getSender()), "agent.localPlatformContains(newMessage.getSender())");
		
		params = new HashMap<>();
		params.put("test_pong_param_key_1", "test_pong_param_val_1");
		assertEquals(newMessage.getSender().getName(), agent.findAid(params).get(0).getName(), "agent.findAid(params) pong = sender");
		
		assertEquals("TestPongAgent", type.getTypeOf(newMessage.getSender()).getName(), "type.getTypeOf(newMessage.getSender())");
		assertEquals(AgentState.PASSIVE, agent.getState(newMessage.getSender()), "agent.getState(newMessage.getSender())");
		
		// service
		assertTrue(service.exists("TestAgentService"), "service.exists('TestAgentService')");
		assertEquals("test-service-attr-val-1", service.getAttributes("TestAgentService").get("test-service-attr-key-1"), "service.getAttributes('TestAgentService')");
		assertEquals("params=123", service.callSynch("TestAgentService", "1", "2", "3"), "service.callSynch('TestAgentService', '1', '2', '3')");
		assertEquals("params=123", service.callAsynch("TestAgentService", "1", "2", "3").get(), "service.callAsynch('TestAgentService', '1', '2', '3')");
		params = new HashMap<>();
		params.put("test-service-attr-key-1", "test-service-attr-val-1");
		assertEquals("TestAgentService", service.find(params).get(0), "service.find(params).get(0)");
		
		// 
		
		System.out.println("[IntegrationTestAgent] tests complete. errorCount = "+errorCount);
	}
	
	private void assertEquals(Object o1, Object o2, String message)
	{
		if(o1 == null)
		{
			System.out.println(message +" ; First parameter is null.");
			errorCount++;
			return;
		}
		
		assertTrue(o1.equals(o2), message+" ; " + "<"+o1+"> and <"+o2+"> should be equal.");
	}
	
	private void assertTrue(boolean exp, String message)
	{
		if(!exp)
		{
			errorCount++;
			System.out.println(message);
		}
	}
}
