package org.jwaf;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AgentIdentifier 
{
	@Id @GeneratedValue
	private int id;
	
	private String name;
	
	private List<URL> addresses;
	private List<AgentIdentifier> resolvers;
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
