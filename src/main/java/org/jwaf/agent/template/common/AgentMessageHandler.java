package org.jwaf.agent.template.common;

import org.jwaf.message.persistence.entity.ACLMessage;

public interface AgentMessageHandler
{
	void handle(ACLMessage message);
}
