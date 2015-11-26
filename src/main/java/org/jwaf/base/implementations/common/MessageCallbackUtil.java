package org.jwaf.base.implementations.common;

import java.lang.reflect.Method;

import org.jwaf.common.exceptions.AgentException;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCallbackUtil
{
	private static Logger log = LoggerFactory.getLogger("MessageCallbackUtil");
	
	public static boolean isValidMessageCallback(Method method)
	{
		// has one parameter of type ACLMessage
		if(method.getParameterCount() == 1)
		{
			if(ACLMessage.class.isAssignableFrom(method.getParameterTypes()[0])) 
			{
				return true;
			}
		}

		log.warn("Invalid message handler method: {}, method annotated as a MessageCallbac does not have one parameter of type ACLMessage.", method.getName());
		
		return false;
	}
	
	public static void handleMessage(AgentMessageHandler handler, ACLMessage message) throws Exception
	{
		try
		{
			handler.handle(message);
		}
		catch (InvocationExceptionWrapper e)
		{
			Exception wrapped = e.getWrapped();
			
			if(wrapped.getCause() instanceof AgentException)
			{
				throw wrapped;
			}
			else
			{
				log.error("Error during message handler invocation.", wrapped);
			}
		}
	}
}
