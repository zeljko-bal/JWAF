package org.jwaf.agent;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.persistence.entity.ACLMessage;

public interface MultiThreadedAgent extends Agent
{
	void _handle(AgentIdentifier aid, ACLMessage newMessage) throws Exception;
}
