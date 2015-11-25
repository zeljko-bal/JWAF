package org.jwaf.agent;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

public interface SingleThreadedAgent extends Agent
{
	void _execute(AgentIdentifier aid) throws Exception;
}