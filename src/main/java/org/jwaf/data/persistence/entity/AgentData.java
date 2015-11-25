package org.jwaf.data.persistence.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentData
{
	@Id
	@XmlElement(required=true)
	private String name;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@Lob
	@XmlElementWrapper
	private Map<String, String> privateData;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@Lob
	@XmlElementWrapper
	private Map<String, String> publicData;
	
	public AgentData()
	{
		privateData = new HashMap<>();
		publicData = new HashMap<>();
	}
	
	public AgentData(String name)
	{
		this();
		this.name = name;
	}
	
	public Map<String, String> get(AgentDataType type)
	{
		switch (type) 
		{
			case PRIVATE:
				return privateData;
			case PUBLIC:
				return publicData;
			default:
				return null;
		}
	}
}
