package org.jwaf.event.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException
public class EventNotFound extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public EventNotFound()
	{}
	
	public EventNotFound(String message)
	{
		super(message);
	}
}