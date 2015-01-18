package org.jwaf.message.persistence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TransportMessage
{	
	@XmlElementWrapper
	@XmlElement(name="AgentIdentifier")
	private List<AgentIdentifier> intendedReceivers;
	
	@XmlElement
	private Date dateSent;
	
	@XmlElementWrapper
	@XmlElement(name="stamp")
	private List<String> stamps;
	
	@XmlElement
	private ACLMessage content;

	public TransportMessage()
    {
		intendedReceivers = new ArrayList<>();
		stamps = new ArrayList<>();
    }
	
	public TransportMessage(ACLMessage content, List<AgentIdentifier> intendedReceivers)
	{
		this();
		this.content = content;
		this.intendedReceivers.addAll(intendedReceivers);
	}

	public List<String> getStamps() 
	{
		return stamps;
	}
	
    public ACLMessage getContent()
    {
		return content;
	}

	public void setContent(ACLMessage content)
	{
		this.content = content;
	}

	public Date getDateSent()
	{
		return dateSent;
	}

	public void setDateSent(Date date)
	{
		this.dateSent = date;
	}
	
	public List<AgentIdentifier> getIntendedReceivers()
	{
		return intendedReceivers;
	}
}
