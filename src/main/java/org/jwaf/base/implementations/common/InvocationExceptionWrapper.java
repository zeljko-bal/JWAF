package org.jwaf.base.implementations.common;

public class InvocationExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	private Exception wrapped;
	
	public InvocationExceptionWrapper(Exception wrapped)
	{
		this.wrapped = wrapped;
	}

	public Exception getWrapped()
	{
		return wrapped;
	}
}
