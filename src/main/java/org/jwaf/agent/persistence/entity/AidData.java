package org.jwaf.agent.persistence.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
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

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AidData
{
	@Id @GeneratedValue
	@XmlTransient
	private Integer id;
	
	@ElementCollection
	@XmlElementWrapper
	@XmlElement(name="address")
	private List<String> addresses;
	
	@ManyToMany(cascade=CascadeType.REFRESH)
	@XmlElementWrapper
	@XmlElement(name="resolver")
	private List<AgentIdentifier> resolvers;
	
	public AidData()
	{
		addresses = new ArrayList<>();
		resolvers = new ArrayList<>();
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
