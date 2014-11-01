package org.jwaf.agent.template.reactive;

import javax.annotation.PostConstruct;

import org.jwaf.agent.AbstractAgent;

public abstract class AbstractReactiveAgent extends AbstractAgent
{
	protected MessageHandlingBlock messageHandlingBlock;

	@PostConstruct
	protected void initializeMessageHandlingBlock()
	{
		messageHandlingBlock = new MessageHandlingBlock(this, messageServices);
	}
	
	@Override
	public void execute()
	{
		messageHandlingBlock.invokeMessageHandlers();
	}
}
