package org.jwaf.common.exceptions;

/**
 * A common supertype of all agent exceptions.
 * 
 * @author zeljko.bal
 */
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
