package org.jwaf.agent.exceptions;

import javax.ejb.ApplicationException;

/**
 * Thrown when a state changing transaction can't be completed due to frequent changes in the database.
 * 
 * @author zeljko.bal
 */
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
