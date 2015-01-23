package org.jwaf.agent.persistence.entity;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentIdentifier 
{
	@Id
	@XmlElement(required=true)
	private String name;
	
	@OneToOne(cascade=CascadeType.ALL)
	private AidData data;
	
	public AgentIdentifier()
	{
		data = new AidData();
	}
	
	public AgentIdentifier(String name)
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
	
	public List<URL> getAddresses()
	{
		return data.getAddresses();
	}
	
	public List<AgentIdentifier> getResolvers()
	{
		return data.getResolvers();
	}
	
	public Map<String, String> getUserDefinedParameters()
	{
		return data.getUserDefinedParameters();
	}
}
