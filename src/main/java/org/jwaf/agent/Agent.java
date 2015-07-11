package org.jwaf.agent;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

public interface Agent
{
	void _execute(AgentIdentifier aid) throws Exception;
	void _setup(AgentIdentifier aid);
	void _onArrival(AgentIdentifier aid);
}
