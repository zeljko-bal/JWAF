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
public class MessageEnvelope
{	
	@XmlElement
	private AgentIdentifier from;
	
	@XmlElementWrapper
	@XmlElement(name="AgentIdentifier")
	private List<AgentIdentifier> to;
	
	@XmlElementWrapper
	@XmlElement(name="AgentIdentifier")
	private List<AgentIdentifier> intended_receiver;
	
	@XmlElement
	private String acl_representation;
	
	@XmlElement
	private Date date;
	
	@XmlElementWrapper
	@XmlElement(name="stamp")
	private List<String> received;
	
	@XmlElement
	private ACLMessage content;

	public MessageEnvelope()
    {
    	to = new ArrayList<>();
    	received = new ArrayList<>();
    }

	public List<String> getReceived() 
	{
		return received;
	}
	
    public ACLMessage getContent()
    {
		return content;
	}

	public void setContent(ACLMessage content)
	{
		this.content = content;
	}

	public String getAcl_representation()
	{
		return acl_representation;
	}

	public void setAcl_representation(String acl_representation)
	{
		this.acl_representation = acl_representation;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public AgentIdentifier getFrom()
	{
		return from;
	}
	
	public void setFrom(AgentIdentifier from)
	{
		this.from = from;
	}

	public List<AgentIdentifier> getToList()
	{
		return to;
	}
	
	public List<AgentIdentifier> getIntended_receiverList()
	{
		return intended_receiver;
	}
}
