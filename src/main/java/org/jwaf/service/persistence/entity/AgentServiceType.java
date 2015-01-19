package org.jwaf.service.persistence.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

@Entity
public class AgentServiceType
{
	@Id
	private String name;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, String> attributes;
	
	public AgentServiceType()
	{
		attributes = new HashMap<>();
	}
	
	public AgentServiceType(String name)
	{
		this();
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map<String, String> getAttributes()
	{
		return attributes;
	}
}
