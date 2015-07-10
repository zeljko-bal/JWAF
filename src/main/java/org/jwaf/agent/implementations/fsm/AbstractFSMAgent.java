package org.jwaf.agent.implementations.fsm;

import org.jwaf.agent.AbstractAgent;

public class AbstractFSMAgent extends AbstractAgent
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
