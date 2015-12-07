package org.jwaf.agent.persistence.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jwaf.common.URLListWrapper;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentIdentifier 
{
	@Id
	@XmlElement(required=true)
	private String name;
	
	@XmlElementWrapper
	@XmlElement(name="address")
	private List<String> addresses;
	
	@XmlElementWrapper
	@XmlElement(name="resolver")
	@Embedded
	private List<AgentIdentifier> resolvers;
	
	public AgentIdentifier()
	{
		addresses = new ArrayList<>();
		resolvers = new ArrayList<>();
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
		return new URLListWrapper(addresses);
	}
	
	public List<AgentIdentifier> getResolvers()
	{
		return resolvers;
	}
}
