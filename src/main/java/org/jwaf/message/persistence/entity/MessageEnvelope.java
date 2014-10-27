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
	@XmlElementWrapper
	@XmlElement(name="AgentIdentifier")
	private List<AgentIdentifier> intended_receiver;
	
	@XmlElement
	private Date date;
	
	@XmlElementWrapper
	@XmlElement(name="stamp")
	private List<String> received;
	
	@XmlElement
	private ACLMessage content;

	public MessageEnvelope()
    {
    	received = new ArrayList<>();
    }
	
	public MessageEnvelope(ACLMessage content, List<AgentIdentifier> intended_receiver)
	{
		this();
		this.content = content;
		this.intended_receiver.addAll(intended_receiver);
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

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public List<AgentIdentifier> getIntended_receiverList()
	{
		return intended_receiver;
	}
}
