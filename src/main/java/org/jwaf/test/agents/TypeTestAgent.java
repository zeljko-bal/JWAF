package org.jwaf.test.agents;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
@TypeAttribute(key="key_1",value="val_1")
public class TypeTestAgent extends AbstractTestAgent
{
	@MessageHandler
	public void test(ACLMessage message)
	{
		HashMap<String, String> params;
		List<String> typeResults;
		
		String myTypeName = getClass().getSimpleName();
		String helperTypeName = SuicidalTestAgentHelper.class.getSimpleName();
		
		AgentIdentifier helperAid = agentDirectory.createAgent(new CreateAgentRequest(helperTypeName));
		
		t.assertEqual(types.find(myTypeName).getName(), myTypeName, "types.find(myTypeName).getName()");
		t.assertEqual("val_1", types.find(myTypeName).getAttributes().get("key_1"), "types.find(myTypeName).getAttributes()");
		t.assertEqual("val_2", types.find(helperTypeName).getAttributes().get("key_2"), "types.find(helperTypeName).getAttributes()");
		t.assertEqual(types.getTypeOf(aid).getName(), myTypeName, "types.getTypeOf(myTypeName)");
		t.assertEqual(types.getTypeOf(aid.getName()).getName(), myTypeName, "types.getTypeOf(myTypeName.getName())");
		t.assertEqual(types.getTypeOf(helperAid).getName(), helperTypeName, "types.getTypeOf(helperTypeName)");
		t.assertEqual(types.getTypeOf(helperAid.getName()).getName(), helperTypeName, "types.getTypeOf(helperTypeName.getName())");
		
		agentDirectory.requestAgentTermination(helperAid.getName());
		
		// findTypeUsingParameterMapTests
		
		params = new HashMap<>();
		params.put("key_1", "val_1");
		typeResults = getTypeNames(types.find(params));
		t.assertTrue(typeResults.contains(myTypeName), "types.find(key_1) contains myTypeName");
		t.assertTrue(typeResults.contains(helperTypeName), "types.find(key_1) contains helperTypeName");
		
		params = new HashMap<>();
		params.put("key_2", "val_2");
		typeResults = getTypeNames(types.find(params));
		t.assertFalse(typeResults.contains(myTypeName), "types.find(key_2) not contains myTypeName");
		t.assertTrue(typeResults.contains(helperTypeName), "types.find(key_2) contains helperTypeName");
		
		params = new HashMap<>();
		params.put("key_1", "val_1");
		params.put("key_2", "val_2");
		typeResults = getTypeNames(types.find(params));
		t.assertFalse(typeResults.contains(myTypeName), "types.find(key_1 && key_2) not contains myTypeName");
		t.assertTrue(typeResults.contains(helperTypeName), "types.find(key_1 && key_2) contains helperTypeName");
		
		params = new HashMap<>();
		params.put("key_1", "val_1");
		params.put("key_2", "wrong_value");
		typeResults = getTypeNames(types.find(params));
		t.assertFalse(typeResults.contains(myTypeName), "types.find(key_1 && wrong_value) not contains myTypeName");
		t.assertFalse(typeResults.contains(helperTypeName), "types.find(key_1 && wrong_value) not contains helperTypeName");
		
		// findTypeUsingQueryTests
		
		typeResults = getTypeNames(types.find(q->q.field("attributes.key_1").equal("val_1")));
		t.assertTrue(typeResults.contains(myTypeName), "types.find(key_1) contains myTypeName");
		t.assertTrue(typeResults.contains(helperTypeName), "types.find(key_1) contains helperTypeName");
		
		typeResults = getTypeNames(types.find(q->q.field("attributes.key_2").equal("val_2")));
		t.assertFalse(typeResults.contains(myTypeName), "types.find(key_1) contains myTypeName");
		t.assertTrue(typeResults.contains(helperTypeName), "types.find(key_1) contains helperTypeName");
		
		typeResults = getTypeNames(types.find(q->
		{
			q.or(q.criteria("attributes.key_1").equal("val_1"), 
				 q.criteria("attributes.key_2").equal("val_2"));
			return q;
		}));
		t.assertTrue(typeResults.contains(myTypeName), "types.find(key_1 || key_2) contains myTypeName");
		t.assertTrue(typeResults.contains(helperTypeName), "types.find(key_1 || key_2) contains helperTypeName");
		
		typeResults = getTypeNames(types.find(q->q.field("attributes.key_1").equal("val_1")
									 .field("attributes.key_2").equal("val_2")));
		t.assertFalse(typeResults.contains(myTypeName), "types.find(key_1 && key_2) contains myTypeName");
		t.assertTrue(typeResults.contains(helperTypeName), "types.find(key_1 && key_2) contains helperTypeName");
		
		typeResults = getTypeNames(types.find(q->q.field("attributes.key_1").equal("val_1")
				 					 .field("attributes.key_2").equal("wrong_value")));
		t.assertFalse(typeResults.contains(myTypeName), "types.find(key_1 && wrong_value) contains myTypeName");
		t.assertFalse(typeResults.contains(helperTypeName), "types.find(key_1 && wrong_value) contains helperTypeName");
		
		t.sendResults(message.getSender());
		self.terminate();
	}
	
	private List<String> getTypeNames(List<AgentType> types)
	{
		return types.stream()
				.map(t->t.getName())
				.collect(Collectors.toList());
	}
}
