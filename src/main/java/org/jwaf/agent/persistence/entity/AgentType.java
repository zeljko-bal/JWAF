package org.jwaf.agent.persistence.entity;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentType
{
	@Id
	@XmlElement(required=true)
	private String name;
	
	@XmlElementWrapper
	private Map<String, String> attributes;
	
	public AgentType()
	{
		attributes = new HashMap<>();
	}
	
	public AgentType(String name)
	{
		this();
		this.name = name;
	}

	public String getName() 
	{
		return name;
	}
	
	public Map<String, String> getAttributes()
	{
		return attributes;
	}
}
