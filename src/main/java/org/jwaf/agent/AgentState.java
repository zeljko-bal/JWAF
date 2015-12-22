package org.jwaf.agent;

public class AgentState 
{
	/**
	 * Agent has no active threads executing.
	 */
	public static final String PASSIVE = "PASSIVE";
	
	/**
	 * Agent is executing on at least one thread.
	 */
	public static final String ACTIVE = "ACTIVE";
	
	/**
	 * Agent has been created and hasn't finished initialization.
	 */
	public static final String INITIALIZING = "INITIALIZING";
	
	/**
	 * Agent is currently in the process of transition to/from another platform.
	 */
	public static final String IN_TRANSIT = "IN_TRANSIT";
}
