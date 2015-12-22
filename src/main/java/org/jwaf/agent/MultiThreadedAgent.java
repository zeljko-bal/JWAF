package org.jwaf.agent;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.persistence.entity.ACLMessage;

/**
 * An agent that can execute on multiple threads. 
 * Messages are passed directly to the _handle method, each on a separate thread.
 * 
 * @author zeljko.bal
 */
public interface MultiThreadedAgent extends Agent
{
	void _handle(AgentIdentifier aid, ACLMessage newMessage) throws Exception;
}
