package org.jwaf.remote.exceptions;

import javax.ejb.ApplicationException;

import org.jwaf.util.exceptions.AgentSuccessException;

@ApplicationException
public class AgentDeparted extends AgentSuccessException
{
	private static final long serialVersionUID = 1L;

	public AgentDeparted(String string)
	{
		super(string);
	}
}
