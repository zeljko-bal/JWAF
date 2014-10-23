package org.jwaf.agent.persistence.entity;


public interface AgentEntityView
{
	public AgentIdentifier getAid();

	public AgentType getType();

	public String getState();

	public boolean hasNewMessages();
}
