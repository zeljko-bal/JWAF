package org.jwaf.agent.entity;

import java.net.URL;
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

@Entity
public class AgentIdentifier 
{
	@Id @GeneratedValue
	private Integer id;

	@Column(unique=true, nullable=false)
	private String name;
	
	@ElementCollection
	private List<URL> addresses;
	
	@ManyToMany(cascade=CascadeType.REFRESH)
	private List<AgentIdentifier> resolvers;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, String> userDefinedParameters;
	
	public AgentIdentifier()
	{}
	
	public AgentIdentifier(String name)
	{
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
	
	@Override
	public boolean equals(Object other) 
	{
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof AgentIdentifier))return false;
	    return name.equals(((AgentIdentifier)other).getName());
	}
	
	public Integer getId() 
	{
		return id;
	}
}
