package org.jwaf.agent.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jwaf.agent.AgentState;
import org.jwaf.message.persistence.entity.ACLMessage;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentEntity implements AgentEntityView
{
	@Id
	@OneToOne(cascade={CascadeType.REFRESH, CascadeType.MERGE}, optional=false)
	@XmlElement(required=true)
	private AgentIdentifier aid;
	
	@ManyToOne(optional=false)
	@XmlElement(required=true)
	private AgentType type;
	
	@ManyToMany(cascade=CascadeType.REFRESH)
	@XmlElementWrapper
	@XmlElement(name="ACLMessage")
	private List<ACLMessage> messages;
	
	@XmlTransient
	private String state;
	
	@XmlTransient
	private boolean hasNewMessages;
	
	public AgentEntity()
	{
		messages = new ArrayList<>();
		state = AgentState.INITIALIZING;
		hasNewMessages = false;
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

	@Override
	public boolean hasNewMessages() 
	{
		return hasNewMessages;
	}

	public void setHasNewMessages(boolean hasNewMessages) 
	{
		this.hasNewMessages = hasNewMessages;
	}
}
