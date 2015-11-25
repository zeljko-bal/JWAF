package org.jwaf.agent;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

public interface Agent
{
	void _setup(AgentIdentifier aid);
	void _onArrival(AgentIdentifier aid);
	void _deserialize(AgentIdentifier aid, String data);
}
