package org.jwaf.agent;

import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.agent.entity.AgentType;

public interface AgentEntityView
{
	public AgentIdentifier getAid();

	public AgentType getType();

	public String getState();

	public boolean hasNewMessages();
}
