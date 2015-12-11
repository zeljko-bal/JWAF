package org.jwaf.base.implementations.common;

import org.jwaf.message.persistence.entity.ACLMessage;

public interface AgentMessageHandler
{
	void handle(ACLMessage message) throws Exception;
}
