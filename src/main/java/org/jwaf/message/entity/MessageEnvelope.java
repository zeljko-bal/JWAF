package org.jwaf.message.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jwaf.agent.entity.AgentIdentifier;

public class MessageEnvelope implements Serializable
{
	private static final long serialVersionUID = 341257798411539923L;
	
	private AgentIdentifier from;
	
	private List<AgentIdentifier> to;
	
	private String acl_representation;
	
	private Date date;
	
	private List<String> received;
	
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
}
