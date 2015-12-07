package org.jwaf.event.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity
public class EventEntity
{
	@Id
	private String name;
	
	private String type;
	
	@Reference(lazy=true)
	private List<AgentIdentifier> registeredAgents;

	public EventEntity()
	{
		registeredAgents = new ArrayList<>();
	}
	
	public EventEntity(String name, String type)
	{
		this();
		this.name = name;
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public List<AgentIdentifier> getRegisteredAgents()
	{
		return registeredAgents;
	}
}
