package org.jwaf.base.implementations.behaviour.exceptions;

public class UndefinedHandlerException extends RuntimeException
{
	private static final long	serialVersionUID	= 1L;
	private String key;
	public UndefinedHandlerException(String key)
	{
		this.key = key;
	}
	public String getKey()
	{
		return key;  
	}
}
