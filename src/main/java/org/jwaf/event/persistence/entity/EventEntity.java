package org.jwaf.event.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Entity
public class EventEntity
{
	@Id
	private String name;
	
	private String type;
	
	@ManyToMany(cascade={CascadeType.REFRESH})
	@JoinTable(name = "Event_aid")
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
