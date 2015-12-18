package org.jwaf.test.agents;

import org.jwaf.base.implementations.behaviour.AbstractBehaviourAgent;
import org.jwaf.test.TestTools;

public abstract class AbstractTestAgent extends AbstractBehaviourAgent
{
	protected TestTools t;
	
	@Override
	protected void postConstruct() 
	{
		super.postConstruct();
		t = new TestTools(log, data, messages);
	}
	
	@Override
	protected void setup()
	{
		super.setup();
		t.setup(self.getType().getName());
	}
}
