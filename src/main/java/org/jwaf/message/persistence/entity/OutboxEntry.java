package org.jwaf.message.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class OutboxEntry
{
	@Id
	private String receiverName;
	
	@OneToOne
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
