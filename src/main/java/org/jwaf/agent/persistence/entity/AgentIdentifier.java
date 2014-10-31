package org.jwaf.agent.persistence.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentIdentifier 
{
	@Id @GeneratedValue
	@XmlTransient
	private Integer id;

	@Column(unique=true, nullable=false)
	@XmlElement(required=true)
	private String name;
	
	@ElementCollection
	@XmlElementWrapper
	@XmlElement(name="address")
	private List<URL> addresses;
	
	@ManyToMany(cascade=CascadeType.REFRESH)
	@XmlElementWrapper
	@XmlElement(name="resolver")
	private List<AgentIdentifier> resolvers;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@XmlElementWrapper
	private Map<String, String> userDefinedParameters;
	
	public AgentIdentifier()
	{
		addresses = new ArrayList<>();
		resolvers = new ArrayList<>();
		userDefinedParameters = new HashMap<>();
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
		return addresses;
	}
	
	public List<AgentIdentifier> getResolvers()
	{
		return resolvers;
	}
	
	public Map<String, String> getUserDefinedParameters()
	{
		return userDefinedParameters;
	}
}
