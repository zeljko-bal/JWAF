package org.jwaf.agent.test;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.AbstractAgent;

@Stateless
@LocalBean
public class IntegrationTestAgent extends AbstractAgent
{
	@Override
	public void execute()
	{
		System.out.println("Test agent activated: name="+aid.getName());
		
		try
		{
			Thread.sleep(6000);
		} 
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("aid is null"+(aid == null));
		System.out.println("executing test agent: "+aid.getName());
		System.out.println("localPlatformContains agent1@platform1: "+(agent.localPlatformContains("agent1@platform1")));
		System.out.println("test end");
		
		message.ignoreAndForgetNewMessages();
	}
}
