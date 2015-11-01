package org.jwaf.base.implementations.reactive;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwaf.base.BaseAgent;
import org.jwaf.base.implementations.common.AgentMessageHandler;
import org.jwaf.base.implementations.common.InvocationExceptionWrapper;
import org.jwaf.base.implementations.common.MessageCallbackUtil;
import org.jwaf.base.implementations.reactive.annotation.DefaultMessageHandler;
import org.jwaf.base.implementations.reactive.annotation.MessageHandler;
import org.jwaf.base.tools.MessageTools;
import org.jwaf.message.persistence.entity.ACLMessage;

public class MessageHandlingTools
{
	private Map<String, AgentMessageHandler> messageHandlers;
	private List<ACLMessage> unhandledMessages;
	private BaseAgent owner;
	private MessageTools messageTools;
	private AgentMessageHandler defaultMessageHandler;
	
	public MessageHandlingTools(BaseAgent owner, MessageTools messageTools)
	{
		unhandledMessages = new ArrayList<>();
		this.owner = owner;
		this.messageTools = messageTools;
		
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
						throw new InvocationExceptionWrapper(e);
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
						throw new InvocationExceptionWrapper(e);
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
	
	public void invokeMessageHandlers() throws Exception
	{
		// for each new message
		for(ACLMessage message : messageTools.getAll())
		{
			AgentMessageHandler handler = messageHandlers.get(message.getPerformative());
			
			if(handler != null)
			{
				// handle with predefined handler
				MessageCallbackUtil.handleMessage(handler, message);
			}
			else
			{
				// save message with no specified handler
				unhandledMessages.add(message);
			}
		}
		
		// if defaultMessageHandler is specified
		if(defaultMessageHandler != null)
		{
			// handle all unhandled message with defauld handler
			for(ACLMessage message : unhandledMessages)
			{
				MessageCallbackUtil.handleMessage(defaultMessageHandler, message);
			}
			unhandledMessages.clear();
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
