package org.jwaf.base.implementations.behaviour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jwaf.base.implementations.behaviour.exceptions.UndefinedHandlerException;
import org.jwaf.base.implementations.common.AgentMessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;

/**
 * A class representing a certain agent behaviour as a set of message handling methods.
 * 
 * @author zeljko.bal
 */
public class AgentBehaviour
{
	private Map<String, AgentMessageHandler> handlers;
	private AgentMessageHandler defaultHandler;
	
	/**
	 * A function that determines the String key of a handler that should handle the given message.
	 */
	private Function<ACLMessage, String> mapToKey;
	
	/**
	 * By default the performative is used as the key.
	 */
	public AgentBehaviour()
	{
		// by default the performative is used as the key
		this(m->m.getPerformative());
	}
	
	/**
	 * @param mapToKey function that determines the String key of a handler that should handle the given message.
	 */
	public AgentBehaviour(Function<ACLMessage, String> mapToKey)
	{
		handlers = new HashMap<>();
		this.mapToKey = mapToKey;
	}
	
	public AgentBehaviour defaultHandler(AgentMessageHandler defaultHandler)
	{
		this.defaultHandler = defaultHandler;
		return this;
	}
	
	public AgentMessageHandler getDefaultHandler()
	{
		return defaultHandler;
	}
	
	public boolean hasDefaultHandler()
	{
		return defaultHandler != null;
	}
	
	public AgentBehaviour handler(String key, AgentMessageHandler handler)
	{
		handlers.put(key, handler);
		return this;
	}
	
	public Map<String, AgentMessageHandler> getHandlers()
	{
		return handlers;
	}
	
	public void handle(ACLMessage message) throws Exception
	{
		String key = mapToKey.apply(message);
		AgentMessageHandler handler = handlers.get(key);
		
		if(handler != null)
		{
			// if a handler is found handle the message
			handler.handle(message);
		}
		else if(hasDefaultHandler())
		{
			// if no handler was found handle the message with a default one if specified
			defaultHandler.handle(message);
		}
		else
		{
			// if no handler could be found throw an exception
			throw new UndefinedHandlerException(key);
		}
	}
	
	public void handle(List<ACLMessage> messages) throws Exception
	{
		for(ACLMessage m : messages)
		{
			handle(m);
		}
	}
}

