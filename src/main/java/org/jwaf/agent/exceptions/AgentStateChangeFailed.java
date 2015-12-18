package org.jwaf.agent.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException
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
