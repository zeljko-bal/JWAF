package org.jwaf.agent.implementations.reactive;

import org.jwaf.agent.BaseAgent;

public abstract class AbstractReactiveAgent extends BaseAgent
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
