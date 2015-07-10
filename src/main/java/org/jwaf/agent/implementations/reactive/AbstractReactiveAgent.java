package org.jwaf.agent.implementations.reactive;

import org.jwaf.agent.AbstractAgent;

public abstract class AbstractReactiveAgent extends AbstractAgent
{
	protected MessageHandlingTools messageHandling;

	@Override
	protected void initializeTools()
	{
		messageHandling = new MessageHandlingTools(this, message);
	}
	
	@Override
	public void execute() throws Exception
	{
		messageHandling.invokeMessageHandlers();
	}
}
