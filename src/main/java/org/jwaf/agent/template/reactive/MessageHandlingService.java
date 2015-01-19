package org.jwaf.agent.template.reactive;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwaf.agent.AbstractAgent;
import org.jwaf.agent.services.MessageServices;
import org.jwaf.agent.template.common.AgentMessageHandler;
import org.jwaf.agent.template.common.MessageCallbackUtil;
import org.jwaf.agent.template.reactive.annotation.DefaultMessageHandler;
import org.jwaf.agent.template.reactive.annotation.MessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;

public class MessageHandlingService
{
	private Map<String, AgentMessageHandler> messageHandlers;
	private List<ACLMessage> unhandledMessages;
	private AbstractAgent owner;
	private MessageServices messageServices;
	private AgentMessageHandler defaultMessageHandler;
	
	public MessageHandlingService(AbstractAgent owner, MessageServices messageServices)
	{
		unhandledMessages = new ArrayList<>();
		this.owner = owner;
		this.messageServices = messageServices;
		
		initializeMessageHandlers();
	}
	
	private void initializeMessageHandlers()
	{
		messageHandlers = new HashMap<>();
		
		// for all owners methods
		for(Method method : owner.getClass().getMethods())
		{
			if(isMessageCallback(method))
			{
				// map method to annotated performative
				messageHandlers.put(method.getAnnotation(MessageHandler.class).value(), (ACLMessage m) -> 
				{
					try
					{
						method.invoke(owner, m);
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				});
			}
			else if(isDefaultMessageCallback(method))
			{
				defaultMessageHandler = (ACLMessage m) -> 
				{
					try
					{
						method.invoke(owner, m);
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				};
			}
		}
	}
	
	private boolean isMessageCallback(Method method)
	{
		// has to be annotated with @MessageHandler
		if(method.isAnnotationPresent(MessageHandler.class))
		{
			return MessageCallbackUtil.isValidMessageCallback(method);
		}
		else
		{
			return false;
		}
	}
	
	private boolean isDefaultMessageCallback(Method method)
	{
		// has to be annotated with @DefaultMessageHandler
		if(method.isAnnotationPresent(DefaultMessageHandler.class))
		{
			return MessageCallbackUtil.isValidMessageCallback(method);
		}
		else
		{
			return false;
		}
	}
	
	public void invokeMessageHandlers()
	{
		// for each new message
		messageServices.getAll().forEach((ACLMessage message) -> 
		{
			AgentMessageHandler handler = messageHandlers.get(message.getPerformative());
			
			if(handler != null)
			{
				// handle with predefined handler
				handler.handle(message);
			}
			else
			{
				// save message with no specified handler
				unhandledMessages.add(message);
			}
		});
		
		// if defaultMessageHandler is specified
		if(defaultMessageHandler != null)
		{
			// handle all unhandled message with defauld handler
			unhandledMessages.forEach((ACLMessage message) -> defaultMessageHandler.handle(message));
		}
	}

	public AgentMessageHandler getDefaultMessageHandler()
	{
		return defaultMessageHandler;
	}

	public void setDefaultMessageHandler(AgentMessageHandler defaultMessageHandler)
	{
		this.defaultMessageHandler = defaultMessageHandler;
	}

	public Map<String, AgentMessageHandler> getMessageHandlers()
	{
		return messageHandlers;
	}

	public List<ACLMessage> getUnhandledMessages()
	{
		return unhandledMessages;
	}
}
