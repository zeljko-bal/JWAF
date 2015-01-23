package org.jwaf.agent.template.common;

import java.lang.reflect.Method;

import org.jwaf.agent.exceptions.AgentSelfTerminatedException;
import org.jwaf.message.persistence.entity.ACLMessage;

public class MessageCallbackUtil
{
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

		// TODO log wrong messae handler
		System.out.println("WARNING: Invalid message handler method: "+ method.getName() +" , method annotated as a MessageCallbac does not have one parameter of type ACLMessage.");

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
			
			if(wrapped.getCause() instanceof AgentSelfTerminatedException)
			{
				throw wrapped;
			}
			else
			{
				wrapped.printStackTrace();
			}
		}
	}
}
