package org.jwaf.remote.exceptions;

import javax.ejb.ApplicationException;

import org.jwaf.util.exceptions.AgentSuccessException;

@ApplicationException
public class AgentTransportSuccessful extends AgentSuccessException
{
	private static final long serialVersionUID = 1L;

	public AgentTransportSuccessful(String string)
	{
		super(string);
	}
}
