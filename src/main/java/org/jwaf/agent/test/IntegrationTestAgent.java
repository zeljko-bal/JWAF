package org.jwaf.agent.test;

import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;

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
	public void setup()
	{
		event.register("state-test-evt"); // TODO check if exists event, timer
		event.subscribe("state-test-evt");
		timer.register("state-test-timer", "state-test-evt", (new ScheduleExpression()).hour("*").minute("*").second("*/10"));
	}
	
	@StateCallback(state="initial", initial=true)
	public void initialState(ACLMessage message)
	{
		System.out.println("initial state: "+message.getPerformative());
		stateHandling.changeState("state1");
	}
	
	@StateCallback(state="state1")
	public void state1(ACLMessage message)
	{
		System.out.println("state1: "+message.getPerformative());
		stateHandling.changeState("state2");
	}
	
	@StateCallback(state="state2")
	public void state2(ACLMessage message)
	{
		System.out.println("state2: "+message.getPerformative());
		stateHandling.changeState("initial");
	}
	
	/*
	@Override
	public void execute()
	{
		System.out.println("IntegrationTestAgent: activated, name="+aid.getName()+", running tests..");
		
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
		
		HashMap<String, String> userParams = new HashMap<>();
		userParams.put("test_param_key_1", "test_param_value_1");
		assertEquals(aid.getName(), agent.findAid(userParams).get(0).getName(), "agent.findAid(userParams) 1");
		
		userParams.put("nonexistent_parameter", "nonexistent_parameter");
		assertTrue(agent.findAid(userParams).isEmpty(), "agent.findAid(userParams) nonexistent");
		
		userParams = new HashMap<>();
		userParams.put("test_param_key_1", "test_param_value_1");
		userParams.put("test_param_key_2", "test_param_value_2");
		assertEquals(aid.getName(), agent.findAid(userParams).get(0).getName(), "agent.findAid(userParams) 1 and 2");
		
		userParams = new HashMap<>();
		userParams.put("test_param_key_1", "test_param_value_1");
		userParams.put("test_param_key_2", "wrong_value");
		assertTrue(agent.findAid(userParams).isEmpty(), "agent.findAid(userParams) wrong value");
		
		userParams = new HashMap<>();
		userParams.put("test_param_key_1", "test_param_value_1");
		userParams.put("wrong_key", "test_param_value_2");
		assertTrue(agent.findAid(userParams).isEmpty(), "agent.findAid(userParams) wrong key");
		
		
		
		message.ignoreAndForgetNewMessages();
		
		System.out.println("IntegrationTestAgent: tests complete.");
	}
	
	private void assertEquals(Object o1, Object o2, String message)
	{
		if(o1 == null)
		{
			System.out.println(message);
			System.out.println("First parameter is null.");
			return;
		}
		
		assertTrue(o1.equals(o2), message+" ; " + "<"+o1+"> and <"+o2+"> should be equal.");
	}
	
	private void assertTrue(boolean exp, String message)
	{
		if(!exp)
		{
			System.out.println(message);
		}
	}
	
	*/
}
