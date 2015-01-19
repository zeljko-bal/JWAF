package org.jwaf.agent.template.fsm;

import javax.annotation.PostConstruct;

import org.jwaf.agent.AbstractAgent;

public class AbstractFSMAgent extends AbstractAgent
{
	protected StateHandlingService stateHandling;
	
	@PostConstruct
	protected void initializeMessageHandlingBlock()
	{
		stateHandling = new StateHandlingService(this, message, self);
	}
	
	@Override
	public void execute()
	{
		stateHandling.invokeStateHandlers();
	}
}
