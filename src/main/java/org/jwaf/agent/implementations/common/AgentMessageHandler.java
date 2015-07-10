package org.jwaf.agent.implementations.common;

import org.jwaf.message.persistence.entity.ACLMessage;

public interface AgentMessageHandler
{
	void handle(ACLMessage message);
}
