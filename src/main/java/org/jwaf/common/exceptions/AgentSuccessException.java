package org.jwaf.common.exceptions;

/**
 * A common supertype for all exceptions that should be treated as successfull agent execution by the platform.
 * 
 * @author zeljko.bal
 */
public class AgentSuccessException extends AgentException
{
	private static final long serialVersionUID = 1L;

	public AgentSuccessException()
	{}
	
	public AgentSuccessException(String msg)
	{
		super(msg);
	}
}
