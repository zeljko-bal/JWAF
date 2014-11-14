package org.jwaf.platform;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotation.resource.EJBJNDIPrefix;

public class PlatformMessageManager
{
	@Inject @EJBJNDIPrefix
	private String agentJNDIPrefix;
	
	public void handle(ACLMessage content)
	{
		try
		{
			PlatformMessageHandler handler = findHandler(content.getUserDefinedParameters().get(MessageParam.PLATFORM_MESSAGE));
			
			handler.handlePlatformMessage(content);
		} 
		catch 
		(NamingException e)
		{
			// TODO log PlatformMessageManager  NamingException
			e.printStackTrace();
		}
	}
	
	private PlatformMessageHandler findHandler(String type) throws NamingException
	{
		return (PlatformMessageHandler)(new InitialContext()).lookup(agentJNDIPrefix + type);
	}
}
