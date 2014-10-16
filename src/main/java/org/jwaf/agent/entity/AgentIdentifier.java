package org.jwaf.agent.entity;

import java.net.URL;
import java.util.List;
import java.util.Map;

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
	
	private String name;
	
	@ElementCollection
	private List<URL> addresses;
	
	@ManyToMany
	private List<AgentIdentifier> resolvers;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, String> userDefinedParameters;
	
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
