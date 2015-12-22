package org.jwaf.agent;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

/**
 * An agent that executes on at most one thread at a time. 
 * Messages are stored in agent's inbox and the agent is notified about new messages by invoking the _execute method.
 * The agent is not notified about the messages he has discovered on his own by retrieving or actively ignoring them.
 * 
 * @author zeljko.bal
 */
public interface SingleThreadedAgent extends Agent
{
	void _execute(AgentIdentifier aid) throws Exception;
}