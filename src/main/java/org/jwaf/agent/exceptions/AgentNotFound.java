package org.jwaf.agent.exceptions;

public class AgentNotFound extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public AgentNotFound()
	{}
	
	public AgentNotFound(String message)
	{
		super(message);
	}
}
