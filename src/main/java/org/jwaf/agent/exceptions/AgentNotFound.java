package org.jwaf.agent.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException
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
