package org.jwaf.base.implementations.reactive;

import org.jwaf.base.BaseAgent;

public abstract class AbstractReactiveAgent extends BaseAgent
{
	protected MessageHandlingTools messageHandling;

	@Override
	protected void postConstruct()
	{
		super.postConstruct();
		messageHandling = new MessageHandlingTools(this, message);
	}
	
	@Override
	public void execute() throws Exception
	{
		messageHandling.invokeMessageHandlers();
	}
}
