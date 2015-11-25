package org.jwaf.remote;

import java.net.URL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jwaf.agent.persistence.entity.AgentEntity;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentTransportData
{
	@XmlElement(required=true)
	private AgentEntity agent;
	
	@XmlElement(required=true)
	private String serializedData;
	
	@XmlElement
	private String platformName;
	
	@XmlElement
	private URL platformAddress;
	
	public AgentTransportData(AgentEntity agent, String serializedData, String platformName, URL platformAddress)
	{
		this.agent = agent;
		this.serializedData = serializedData;
		this.platformName = platformName;
		this.platformAddress = platformAddress;
	}
	
	public AgentEntity getAgent()
	{
		return agent;
	}
	
	public String getSerializedData()
	{
		return serializedData;
	}

	public String getPlatformName() {
		return platformName;
	}

	public URL getPlatformAddress() {
		return platformAddress;
	}
}
