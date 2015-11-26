package org.jwaf.remote.persistence.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.URLListWrapper;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentPlatform
{
	@Id
	@XmlElement
	private String name;
	
	@XmlElement
	private URL address;
	
	@ElementCollection
	@XmlElementWrapper
	@XmlElement(name="address")
	private List<String> messageTransportAddresses;
	
	@ManyToMany(cascade=CascadeType.REFRESH)
	@XmlElementWrapper
	@XmlElement(name="aid")
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
		this.address = address;
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
		return address;
	}

	public void setAddress(URL address)
	{
		this.address = address;
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
