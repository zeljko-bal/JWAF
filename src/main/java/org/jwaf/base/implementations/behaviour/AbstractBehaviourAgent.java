package org.jwaf.base.implementations.behaviour;

import org.jwaf.base.BaseAgent;


public class AbstractBehaviourAgent extends BaseAgent
{
	protected BehaviourTools behaviours;
	
	@Override
	protected void postConstruct()
	{
		super.postConstruct();
		behaviours = new BehaviourTools(this, data, message, log, true);
	}
	
	@Override
	public void execute() throws Exception
	{
		behaviours.handleAll();
	}
}
