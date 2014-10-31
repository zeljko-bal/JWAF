package org.jwaf.agent.exception;

public class AgentSelfTerminatedException extends RuntimeException
{
	private static final long serialVersionUID = 614203549501674787L;

	public AgentSelfTerminatedException(String string)
	{
		super(string);
	}
}
