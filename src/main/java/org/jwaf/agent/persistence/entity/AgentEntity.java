package org.jwaf.agent.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jwaf.agent.AgentState;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentEntity implements AgentEntityView
{
	@Id
	@XmlElement(required=true)
	@Reference
	private AgentIdentifier aid;
	
	@XmlElement(required=true)
	@Reference
	private AgentType type;
	
	@XmlTransient
	private String state;
	
	@XmlElementWrapper
	@XmlElement(name="ACLMessage")
	@Reference
	private List<ACLMessage> messages;
	
	@XmlTransient
	private boolean hasNewMessages;
	
	@XmlTransient
	private Integer activeInstances;
	
	public AgentEntity()
	{
		messages = new ArrayList<>();
		state = AgentState.INITIALIZING;
		hasNewMessages = false;
		activeInstances = 0;
	}
	
	public AgentEntity(AgentType type, AgentIdentifier aid)
	{
		this();
		this.type = type;
		this.aid = aid;
	}
	
	@Override
	public AgentIdentifier getAid() 
	{
		return aid;
	}
	
	public void setAid(AgentIdentifier aid) 
	{
		this.aid = aid;
	}
	
	@Override
	public AgentType getType() 
	{
		return type;
	}
	
	public void setType(AgentType type)
	{
		this.type = type;
	}
	
	public List<ACLMessage> getMessages() 
	{
		return messages;
	}
	
	@Override
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
	
	public Integer getActiveInstances()
	{
		return activeInstances;
	}
	
	public void setActiveInstances(Integer activeInstances)
	{
		this.activeInstances = activeInstances;
	}
}
