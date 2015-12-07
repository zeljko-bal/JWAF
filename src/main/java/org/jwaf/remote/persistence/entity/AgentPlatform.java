package org.jwaf.remote.persistence.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.URLListWrapper;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentPlatform
{
	@Id
	@XmlElement
	private String name;
	
	@XmlElement
	private String address;
	
	@XmlElementWrapper
	@XmlElement(name="address")
	private List<String> messageTransportAddresses;
	
	@XmlElementWrapper
	@XmlElement(name="aid")
	@Reference(lazy=true)
	private List<AgentIdentifier> agentIds;
	
	public AgentPlatform()
	{
		agentIds = new ArrayList<>();
		messageTransportAddresses = new ArrayList<>();
	}

	public AgentPlatform(String name, URL address)
	{
		this();
		this.name = name;
		setAddress(address);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public URL getAddress()
	{
		try
		{
			return new URL(address);
		}
		catch(MalformedURLException e)
		{
			String plName = name != null ? name : "NO_NAME";
			throw new RuntimeException("AgentPlatform <"+plName+">: address value is not a valid URL.", e);
		}
	}

	public void setAddress(URL address)
	{
		this.address = address.toExternalForm();
	}

	public List<AgentIdentifier> getAgentIds()
	{
		return agentIds;
	}
	
	public List<URL> getMTAddresses()
	{
		return new URLListWrapper(messageTransportAddresses);
	}
}
