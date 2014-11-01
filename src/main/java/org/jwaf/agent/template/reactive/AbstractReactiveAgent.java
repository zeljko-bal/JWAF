package org.jwaf.agent.template.reactive;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwaf.agent.AbstractAgent;
import org.jwaf.agent.template.reactive.annotation.HandleMessage;
import org.jwaf.message.persistence.entity.ACLMessage;

public abstract class AbstractReactiveAgent extends AbstractAgent
{
	protected Map<String, AgentMessageHandler> messageHandlers;
	protected List<ACLMessage> unhandledMessages;
	
	public AbstractReactiveAgent()
	{
		if(messageHandlers == null)
		{
			initializeMessageHandlers();
		}
	}
	
	private void initializeMessageHandlers()
	{
		messageHandlers = new HashMap<>();
		
		// for all methods
		for(Method method : getClass().getMethods())
		{
			// if method is valid
			if(isValidMessageCallback(method))
			{
				// map method to annotated performative
				messageHandlers.put(method.getAnnotation(HandleMessage.class).value(), (ACLMessage m) -> 
				{
					try
					{
						method.invoke(this, m);
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				});
			}
		}
	}
	
	private boolean isValidMessageCallback(Method method)
	{
		// has to be annotated with @HandleMessage
		if(method.isAnnotationPresent(HandleMessage.class)) 
		{
			// has one parameter of type ACLMessage
			if(method.getParameterCount() == 1)
			{
				if(ACLMessage.class.isAssignableFrom(method.getParameterTypes()[0])) 
				{
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void execute()
	{
		invokeMessageHandlers();
	}

	private void invokeMessageHandlers()
	{
		// for each new message
		messageServices.getMessages().forEach((ACLMessage message) -> 
		{
			AgentMessageHandler handler = messageHandlers.get(message.getPerformative());
			if(handler != null)
			{
				// handle with predefined handler
				handler.handle(message);
			}
			else
			{
				// save messages with no specified handler
				unhandledMessages.add(message);
			}
		});
		
		// handle all unhandled messages with defauld handler
		unhandledMessages.forEach((ACLMessage message) -> defaultMessageHandler(message));
	}

	protected void defaultMessageHandler(ACLMessage message)
	{/* no-op */}
}
