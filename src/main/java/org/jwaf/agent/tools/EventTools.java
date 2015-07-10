package org.jwaf.agent.tools;

import java.io.Serializable;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.event.management.EventManager;

public class EventTools
{
	private EventManager eventManager;
	private AgentIdentifier aid;
	
	public EventTools(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}

	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}
	
	public boolean exists(String name)
	{
		return eventManager.exists(name);
	}
	
	public void register(String name)
	{
		eventManager.register(name);
	}

	public void register(String name, String type)
	{
		eventManager.register(name, type);
	}

	public void unregister(String name)
	{
		eventManager.unregister(name);
	}

	public void subscribe(String eventName)
	{
		eventManager.subscribe(aid.getName(), eventName);
	}

	public void unsubscribe(String eventName)
	{
		eventManager.unsubscribe(aid.getName(), eventName);
	}

	public void fire(String eventName, String content)
	{
		eventManager.fire(eventName, content);
	}
	
	public void fire(String eventName, Serializable content)
	{
		eventManager.fire(eventName, content);
	}
}
