package org.jwaf.message.persistence.entity;

import javax.xml.bind.annotation.XmlTransient;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class OutboxEntry
{
	@Id
	@XmlTransient
	private Integer id;
	
	private String receiverName;
	
	@Embedded
	private ACLMessage message;
	
	public OutboxEntry()
	{}
	
	public OutboxEntry(String receiverName, ACLMessage message)
	{
		this.message = message;
		this.receiverName = receiverName;
	}

	public ACLMessage getMessage()
	{
		return message;
	}

	public void setMessage(ACLMessage message)
	{
		this.message = message;
	}

	public String getReceiverName()
	{
		return receiverName;
	}

	public void setReceiverName(String receiverName)
	{
		this.receiverName = receiverName;
	}
}
