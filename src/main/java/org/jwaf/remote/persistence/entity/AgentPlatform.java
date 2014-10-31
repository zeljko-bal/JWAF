package org.jwaf.remote.persistence.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentPlatform
{
	@Id @GeneratedValue
	@XmlTransient
	private Integer id;
	
	@XmlElement
	private String name;
	
	@ManyToMany(cascade=CascadeType.REFRESH)
	@XmlElementWrapper
	@XmlElement(name="aid")
	private List<AgentIdentifier> agentAids;
	
	@XmlElement
	private URL address;
	
	public AgentPlatform()
	{
		this.agentAids = new ArrayList<>();
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

	public List<AgentIdentifier> getAgentAids()
	{
		return agentAids;
	}
}
