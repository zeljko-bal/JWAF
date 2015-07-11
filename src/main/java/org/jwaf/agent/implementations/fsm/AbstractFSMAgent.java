package org.jwaf.agent.implementations.fsm;

import org.jwaf.agent.BaseAgent;

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
