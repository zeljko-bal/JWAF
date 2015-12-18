package org.jwaf.test.agents;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.base.tools.Persistent;
import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.message.persistence.entity.ACLMessage;

import com.mongodb.client.model.Filters;

@Stateless
@LocalBean
@TypeAttribute(key="key_1",value="val_1")
public class SelfDataTestAgent extends AbstractTestAgent
{
	private static final String TEST_DATA = "MY_TEST_DATA";
	
	@Persistent
	private String persistentString = "initial string";
	
	@Persistent
	private int persistentInt = 3;
	
	@MessageHandler
	public void test(ACLMessage message)
	{
		data.map(TEST_DATA).put("employer", message.getSender().getName());
		
		t.assertNotEqual(aid, null, "aid not null");
		t.assertEqual(aid.getAddresses().get(0), localPlatformAddress, "aid.getAddresses().get(0) == localPlatformAddress");
		
		t.assertEqual(AgentState.ACTIVE, self.getState(), "self.getState() == ACTIVE");
		t.assertEqual(getClass().getSimpleName(), self.getType().getName(), "self.getType().getName() == getClass().getSimpleName()");
		t.assertEqual("val_1", self.getType().getAttributes().get("key_1"), "self.getType().getAttributes()");
		
		data.map(TEST_DATA).put("private_key", "private_val");
		t.assertEqual("private_val", data.map(TEST_DATA).get("private_key"), "data.map(TEST_DATA).get('private_key')");
		
		data.getPublicDataMap().put("public_key", "public_val");
		t.assertEqual(data.getPublicDataMap().get("public_key"), "public_val", "data.getPublicDataMap().get('public_key')");
		
		persistentString = "persistentString";
		persistentInt = 7;
		
		behaviours.changeTo("phase2");
		
		messages.send(new ACLMessage().addReceivers(aid));
	}
	
	@MessageHandler(behaviour="phase2")
	public void phase2(ACLMessage message)
	{
		// data
		t.assertEqual(data.map(TEST_DATA).get("private_key"), "private_val", "data.map(TEST_DATA).get('private_key') second time");
		t.assertEqual(data.getPublicDataMap().get("public_key"), "public_val", "data.getPublicDataMap().get('public_key') second time");
		
		// TODO test complex data and queries
		
		t.assertEqual(persistentString, "persistentString", "persistentString");
		t.assertEqual(persistentInt, 7, "persistentString");
		
		// agentDirectory
		t.assertEqual(agentDirectory.getState(aid), AgentState.ACTIVE, "agentDirectory.getState(aid) == ACTIVE");
		t.assertEqual(agentDirectory.getState(aid.getName()), AgentState.ACTIVE, "agentDirectory.getState(aid.getName()) == ACTIVE");
		t.assertTrue(agentDirectory.localPlatformContains(aid), "agentDirectory.localPlatformContains(aid)");
		t.assertTrue(agentDirectory.localPlatformContains(aid.getName()), "agentDirectory.localPlatformContains(aid.getName())");
		t.assertEqual(agentDirectory.locationOf(aid), localPlatformName, "agentDirectory.locationOf(aid) == localPlatformName");
		t.assertEqual(agentDirectory.locationOf(aid.getName()), localPlatformName, "agentDirectory.locationOf(aid.getName()) == localPlatformName");
		
		AgentIdentifier aidResult =  agentDirectory.findAid(aid.getName());
		t.assertEqual(aid.getName(), aidResult.getName(), "agentDirectory.findAid(aid.getName())");
		
		List<AgentIdentifier> aidResults = agentDirectory.findAgentsByPublicData(Filters.eq("public_key", "public_val"));
		t.assertTrue(aidResults.contains(aid), "agentDirectory.findAgentsByPublicData()");
		
		t.assertEqual("public_val", agentDirectory.getPublicData(aid.getName()).get("public_key"), "agentDirectory.getPublicData(aid.getName())");
		
		String employer = data.map(TEST_DATA).get("employer");
		t.sendResults(new AgentIdentifier(employer));
		self.terminate();
	}
}
