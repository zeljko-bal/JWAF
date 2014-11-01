package org.jwaf.agent.template.reactive;

import org.jwaf.message.persistence.entity.ACLMessage;

public interface AgentMessageHandler
{
	void handle(ACLMessage message);
}
