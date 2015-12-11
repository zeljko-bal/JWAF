package org.jwaf.base.implementations.behaviour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jwaf.base.implementations.behaviour.exceptions.UndefinedHandlerException;
import org.jwaf.base.implementations.common.AgentMessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;

public class AgentBehaviour
{
	private Map<String, AgentMessageHandler> handlers;
	private AgentMessageHandler defaultHandler;
	private Function<ACLMessage, String> mapToKey;
	
	public AgentBehaviour()
	{
		this(m->m.getPerformative());
	}
	
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
			handler.handle(message);
		}
		else if(hasDefaultHandler())
		{
			defaultHandler.handle(message);
		}
		else
		{
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

