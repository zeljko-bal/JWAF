package org.jwaf.agent.template.fsm;

import javax.annotation.PostConstruct;

import org.jwaf.agent.AbstractAgent;

public class AbstractFSMAgent extends AbstractAgent
{
	protected StateHandlingService stateHandling;
	
	@PostConstruct
	protected void postConstruct()
	{
		super.postConstruct();
		stateHandling = new StateHandlingService(this, message, self, log);
	}
	
	@Override
	public void execute() throws Exception
	{
		stateHandling.invokeStateHandlers();
	}
}
