package org.jwaf.agent.exceptions;

import javax.ejb.ApplicationException;

import org.jwaf.common.exceptions.AgentSuccessException;

@ApplicationException
public class AgentSelfTerminatedException extends AgentSuccessException
{
	private static final long serialVersionUID = 614203549501674787L;

	public AgentSelfTerminatedException(String string)
	{
		super(string);
	}
}
