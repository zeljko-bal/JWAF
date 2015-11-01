package org.jwaf.base.tools;

import org.jwaf.agent.management.AidManager;

public class PlatformTools
{
	private AidManager aidManager;
	
	public PlatformTools(AidManager aidManager)
	{
		this.aidManager = aidManager;
	}
	
	public void cleanUpDatabase()
	{
		aidManager.cleanUp();
	}
}
