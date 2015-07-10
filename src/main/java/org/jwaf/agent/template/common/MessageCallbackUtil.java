package org.jwaf.agent.template.common;

import java.lang.reflect.Method;

import org.jwaf.agent.exceptions.AgentSelfTerminatedException;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.remote.exceptions.AgentTransportFailed;
import org.jwaf.remote.exceptions.AgentTransportSuccessful;
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
			
			if(wrapped.getCause() instanceof AgentSelfTerminatedException || wrapped.getCause() instanceof AgentTransportSuccessful || wrapped.getCause() instanceof AgentTransportFailed)
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
