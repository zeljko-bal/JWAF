package org.jwaf.agent.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.jwaf.message.entity.ACLMessage;



@Entity
public class AgentEntity
{
	@Id @GeneratedValue
	private Integer id;
	
	@OneToOne
	private AgentIdentifier aid;
	
	@ManyToOne
	private AgentType type;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, Serializable> privateData;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, Serializable> publicData;
	
	@ManyToMany
	private List<ACLMessage> messages;
	
	private String state;
	
	private boolean hasNewMessages;

	public AgentIdentifier getAid() 
	{
		return aid;
	}

	public void setAid(AgentIdentifier aid) 
	{
		this.aid = aid;
	}

	public AgentType getType() 
	{
		return type;
	}

	public void setType(AgentType type)
	{
		this.type = type;
	}

	public Map<String, Serializable> getPrivateData()
	{
		return privateData;
	}

	public Map<String, Serializable> getPublicData() 
	{
		return publicData;
	}

	public List<ACLMessage> getMessages() 
	{
		return messages;
	}

	public String getState() 
	{
		return state;
	}

	public void setState(String state) 
	{
		this.state = state;
	}

	public boolean hasNewMessages() 
	{
		return hasNewMessages;
	}

	public void setHasNewMessages(boolean hasNewMessages) 
	{
		this.hasNewMessages = hasNewMessages;
	}
}
