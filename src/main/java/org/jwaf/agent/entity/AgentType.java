package org.jwaf.agent.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AgentType
{
	@Id @GeneratedValue
	private Integer id;
	
	@Column(unique=true, nullable=false)
	private String name;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, String> attributes;
	
	public AgentType()
	{}
	
	public AgentType(String name)
	{
		this.name = name;
	}

	public String getName() 
	{
		return name;
	}
}
