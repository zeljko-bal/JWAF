package org.jwaf.remote.exceptions;

import javax.ejb.ApplicationException;

import org.jwaf.util.exceptions.AgentException;

@ApplicationException
public class AgentTransportFailed extends AgentException
{
	private static final long serialVersionUID = 1L;
	
	public AgentTransportFailed(String string)
	{
		super(string);
	}
}
