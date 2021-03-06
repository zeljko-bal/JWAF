package org.jwaf.message.persistence.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.bson.types.ObjectId;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.util.SerializationUtils;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * A class representing a FIPA ACL message.
 * 
 * @author zeljko.bal
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ACLMessage
{
	@Id
	@XmlTransient
	private ObjectId id;
	
	@XmlElement
	private String performative;
	
	@XmlElement
	@Embedded
	private AgentIdentifier sender;
	
	@XmlElementWrapper
	@XmlElement(name="AgentIdentifier")
	@Embedded
	private List<AgentIdentifier> receiver;
	 
	@XmlElement
	private String content;
	
	@XmlElement
	private String reply_with;
	
	@XmlElement
	private Date reply_by;
	
	@XmlElement
	private String reply_to;
	
	@XmlElementWrapper
	@XmlElement(name="AgentIdentifier")
	@Embedded
	private List<AgentIdentifier> in_reply_to;
	
	@XmlElement
	private String language;
	
	@XmlElement
	private String encoding;
	
	@XmlElement
	private String ontology;
	
	@XmlElement
	private String protocol;
	
	@XmlElement
	private String conversation_id;

	@XmlElementWrapper
	@XmlElement(name="parameter")
	private Map<String, String> user_defined_parameters;
    
    @XmlTransient
    private int unreadCount;
    
    public ACLMessage()
    {
    	this.receiver = new ArrayList<>();
    	this.in_reply_to = new ArrayList<>();
    	this.user_defined_parameters = new HashMap<>();
    }
    
    public ACLMessage(String id)
    {
    	this.id = new ObjectId(id);
    }
    
	public ACLMessage(String performative, AgentIdentifier sender)
	{
		this();
		this.performative = performative;
		this.sender = sender;
	}
    
	public ACLMessage(String performative, AgentIdentifier sender, String content)
	{
		this(performative, sender);
		this.content = content;
	}
	
	public ACLMessage(String performative, AgentIdentifier sender,
			String content,
			String reply_with, Date reply_by, String reply_to,
			String language,
			String encoding, String ontology, String protocol,
			String conversation_id)
	{
		this(performative, sender, content);
		this.reply_with = reply_with;
		this.reply_by = reply_by;
		this.reply_to = reply_to;
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversation_id = conversation_id;
	}
	
	public String getId()
	{
		return id.toHexString();
	}

	public String getPerformative()
	{
		return performative;
	}

	public ACLMessage setPerformative(String performative)
	{
		this.performative = performative;
		return this;
	}

	public AgentIdentifier getSender()
	{
		return sender;
	}

	public ACLMessage setSender(AgentIdentifier sender)
	{
		this.sender = sender;
		return this;
	}

	public List<AgentIdentifier> getReceiverList()
	{
		return receiver;
	}
	
	public ACLMessage addReceivers(AgentIdentifier... receivers)
	{
		for(AgentIdentifier aid : receivers)
		{
			receiver.add(aid);
		}
		return this;
	}

	public String getContent()
	{
		return content;
	}
	
	public Serializable getContentAsObject()
	{
		return SerializationUtils.deSerialize(getContent());
	}

	public ACLMessage setContent(String content)
	{
		this.content = content;
		return this;
	}
	
	public ACLMessage setContentAsObject(Serializable content)
	{
		return setContent(SerializationUtils.serialize(content));
	}

	public String getReply_with()
	{
		return reply_with;
	}

	public ACLMessage setReply_with(String reply_with)
	{
		this.reply_with = reply_with;
		return this;
	}

	public Date getReply_by()
	{
		return reply_by;
	}

	public ACLMessage setReply_by(Date reply_by)
	{
		this.reply_by = reply_by;
		return this;
	}

	public String getReply_to()
	{
		return reply_to;
	}

	public ACLMessage setReply_to(String reply_to)
	{
		this.reply_to = reply_to;
		return this;
	}

	public List<AgentIdentifier> getIn_reply_toList()
	{
		return in_reply_to;
	}

	public String getLanguage()
	{
		return language;
	}

	public ACLMessage setLanguage(String language)
	{
		this.language = language;
		return this;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public ACLMessage setEncoding(String encoding)
	{
		this.encoding = encoding;
		return this;
	}

	public String getOntology()
	{
		return ontology;
	}

	public ACLMessage setOntology(String ontology)
	{
		this.ontology = ontology;
		return this;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public ACLMessage setProtocol(String protocol)
	{
		this.protocol = protocol;
		return this;
	}

	public String getConversation_id()
	{
		return conversation_id;
	}

	public ACLMessage setConversation_id(String conversation_id)
	{
		this.conversation_id = conversation_id;
		return this;
	}

	public Map<String, String> getUserDefinedParameters()
	{
		return user_defined_parameters;
	}

	public int getUnreadCount()
	{
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount)
	{
		this.unreadCount = unreadCount;
	}
}
