package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class AgentCreationTestAgent extends AbstractTestAgent
{
	@MessageHandler
	public void initial(ACLMessage message) throws InterruptedException
	{
		String helperTypeName = SuicidalTestAgentHelper.class.getSimpleName();
		AgentIdentifier helperAid = agentDirectory.createAgent(new CreateAgentRequest(helperTypeName));
		
		t.assertTrue(agentDirectory.localPlatformContains(helperAid), "agentDirectory.localPlatformContains(helperAid)");
		t.assertEqual(agentDirectory.getState(helperAid), AgentState.PASSIVE, "agentDirectory.getState(helperAid) == PASSIVE");
		t.assertEqual(types.getTypeOf(helperAid).getName(), helperTypeName, "types.getTypeOf(created helperTypeName)");
		
		log.info("sending termination request");
		agentDirectory.requestAgentTermination(helperAid.getName());
		
		log.info("waiting for agent to terminate");
		t.assertFalse(()->agentDirectory.localPlatformContains(helperAid), 10000, "agent deleted");
		
		t.sendResults(message.getSender());
		self.terminate();
	}
}
