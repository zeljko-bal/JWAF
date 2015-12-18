package org.jwaf.test.agents;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class ServiceTestAgent extends AbstractTestAgent
{
	@MessageHandler
	public void test(ACLMessage message) throws InterruptedException, ExecutionException
	{
		HashMap<String, String> params;
		List<String> serviceResults;
		t.assertTrue(services.exists("TestAgentService"), "service.exists('TestAgentService')");
		t.assertEqual(services.getAttributes("TestAgentService").get("key_1"), "val_1", "service.getAttributes('TestAgentService')");
		t.assertEqual(services.callSynch("TestAgentService", "1", "2", "3"), "params=123", "service.callSynch('TestAgentService', '1', '2', '3')");
		t.assertEqual(services.callAsynch("TestAgentService", "1", "2", "3").get(), "params=123", "service.callAsynch('TestAgentService', '1', '2', '3')");
		
		params = new HashMap<>();
		params.put("key_1", "val_1");
		serviceResults = services.find(params);
		t.assertTrue(serviceResults.contains("TestAgentService"), "service.find(key_1)");
		
		params = new HashMap<>();
		params.put("key_1", "wrong_value");
		serviceResults = services.find(params);
		t.assertFalse(serviceResults.contains("TestAgentService"), "service.find(wrong_value)");
		
		serviceResults = services.find(q->q.field("attributes.key_1").equal("val_1"));
		t.assertTrue(serviceResults.contains("TestAgentService"), "service.find(key_1) query");
		
		serviceResults = services.find(q->q.field("attributes.key_1").equal("wrong_value"));
		t.assertFalse(serviceResults.contains("TestAgentService"), "service.find(wrong_value) query");
		
		t.sendResults(message.getSender());
		self.terminate();
	}
}
