package org.jwaf.base.implementations.fsm;

import org.jwaf.base.BaseAgent;

public class AbstractFSMAgent extends BaseAgent
{
	protected StateHandlingTools stateHandling;
	
	@Override
	protected void postConstruct()
	{
		super.postConstruct();
		stateHandling = new StateHandlingTools(this, message, data, log);
	}
	
	@Override
	public void execute() throws Exception
	{
		stateHandling.invokeStateHandlers();
	}
}
