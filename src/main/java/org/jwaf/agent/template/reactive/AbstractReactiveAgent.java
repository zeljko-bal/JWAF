package org.jwaf.agent.template.reactive;

import javax.annotation.PostConstruct;

import org.jwaf.agent.AbstractAgent;

public abstract class AbstractReactiveAgent extends AbstractAgent
{
	protected MessageHandlingService messageHandlingService;

	@PostConstruct
	protected void initializeMessageHandlingBlock()
	{
		messageHandlingService = new MessageHandlingService(this, message);
	}
	
	@Override
	public void execute()
	{
		messageHandlingService.invokeMessageHandlers();
	}
}
