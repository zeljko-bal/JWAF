package org.jwaf.message.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jwaf.agent.entity.AgentIdentifier;

@Entity
public class ACLMessage implements Serializable
{
	private static final long serialVersionUID = -1428088846499079746L;

	@Id
	@GeneratedValue
	private Integer id;
	
	private String performative;
	
	@ManyToOne
	private AgentIdentifier sender;
	
	@ManyToMany
	private List<AgentIdentifier> receiver;
	
	@Lob 
	@Basic(fetch=FetchType.LAZY)
	private Serializable content;
	
	private String reply_with;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date reply_by;
	
	private String reply_to;
	
	@ManyToMany
	private List<AgentIdentifier> in_reply_to;
	
	private String language;
	private String encoding;
	private String ontology;
	private String protocol;
	private String conversation_id;

    @ElementCollection
	private Map<String, String> user_defined_parameters;
    
    private Integer unreadCount;
    
    public ACLMessage()
    {}
    
	public ACLMessage(String performative, AgentIdentifier sender, Serializable content)
	{
		this(performative, sender,
				content, 
				null, null, null, null, null, null, null, null);
	}
	
	public ACLMessage(String performative, AgentIdentifier sender,
			Serializable content,
			String reply_with, Date reply_by, String reply_to,
			String language,
			String encoding, String ontology, String protocol,
			String conversation_id)
	{
		this.performative = performative;
		this.sender = sender;
		this.receiver = new ArrayList<>();
		this.content = content;
		this.reply_with = reply_with;
		this.reply_by = reply_by;
		this.reply_to = reply_to;
		this.in_reply_to = new ArrayList<>();
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversation_id = conversation_id;
		this.user_defined_parameters = new HashMap<>();
	}
	
	public int getId()
	{
		return id;
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

	public Serializable getContent()
	{
		return content;
	}

	public ACLMessage setContent(Serializable content)
	{
		this.content = content;
		return this;
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

	public Integer getUnreadCount() 
	{
		return unreadCount;
	}

	public void setUnreadCount(Integer unreadCount) 
	{
		this.unreadCount = unreadCount;
	}
}
