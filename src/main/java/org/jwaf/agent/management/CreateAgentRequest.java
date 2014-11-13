package org.jwaf.agent.management;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateAgentRequest
{
	@XmlElement
	private String type;
	
	@XmlElementWrapper
	@XmlElement(name="parameter")
	private Map<String, String> params;
	
	public CreateAgentRequest(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Map<String, String> getParams()
	{
		return params;
	}
}
