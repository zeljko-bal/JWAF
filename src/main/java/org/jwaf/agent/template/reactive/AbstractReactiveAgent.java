package org.jwaf.agent.template.reactive;

import javax.annotation.PostConstruct;

import org.jwaf.agent.AbstractAgent;

public abstract class AbstractReactiveAgent extends AbstractAgent
{
	protected MessageHandlingTools messageHandling;

	@PostConstruct
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
