package org.jwaf.common.exceptions;

public class AgentException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public AgentException()
	{}
	
	public AgentException(String msg)
	{
		super(msg);
	}
}
