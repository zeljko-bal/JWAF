package org.jwaf.base.implementations.fsm;

import org.jwaf.base.BaseAgent;

public class AbstractFSMAgent extends BaseAgent
{
	protected StateHandlingTools stateHandling;
	
	@Override
	protected void initializeTools()
	{
		stateHandling = new StateHandlingTools(this, message, self, log);
	}
	
	@Override
	public void execute() throws Exception
	{
		stateHandling.invokeStateHandlers();
	}
}
