package org.jwaf.base.implementations.common;

import java.lang.reflect.Method;

import org.jwaf.message.persistence.entity.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCallbackUtil
{
	private static Logger log = LoggerFactory.getLogger("MessageCallbackUtil");
	
	/**
	 * A valid message callback must have exactly one parameter of type {@link ACLMessage}.
	 * 
	 * @param method callback to be examined
	 * @return true if the method is a valid message callback
	 */
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
}
