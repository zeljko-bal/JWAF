package org.jwaf.agent.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentType
{
	@Id @GeneratedValue
	@XmlTransient
	private Integer id;
	
	@Column(unique=true, nullable=false)
	@XmlElement(required=true)
	private String name;
	
	@ElementCollection(fetch=FetchType.LAZY)
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
