package org.jwaf.agent.exceptions;

public class AgentStateChangeFailed extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public AgentStateChangeFailed()
	{}
	
	public AgentStateChangeFailed(String message)
	{
		super(message);
	}
}
