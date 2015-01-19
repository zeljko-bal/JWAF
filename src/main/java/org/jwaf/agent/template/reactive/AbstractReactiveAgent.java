package org.jwaf.agent.template.reactive;

import javax.annotation.PostConstruct;

import org.jwaf.agent.AbstractAgent;

public abstract class AbstractReactiveAgent extends AbstractAgent
{
	protected MessageHandlingService messageHandling;

	@PostConstruct
	protected void initializeMessageHandlingService()
	{
		messageHandling = new MessageHandlingService(this, message);
	}
	
	@Override
	public void execute()
	{
		messageHandling.invokeMessageHandlers();
	}
}
