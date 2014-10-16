package org.jwaf.message.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwaf.agent.entity.AgentIdentifier;

public class ACLMessageQuery
{
	private String performative = null;
	
	private AgentIdentifier sender = null;
	
	private String reply_with = null;
	private Date reply_by = null;
	private String reply_to = null;
	private List<AgentIdentifier> in_reply_to;
	
	private String language = null;
	private String encoding = null;
	private String ontology = null;
	private String protocol = null;
	private String conversation_id = null;
	
	private Map<String, String> userDefinedParameters;
	
	private int maxCount;
	
	public ACLMessageQuery()
	{
		this.in_reply_to = new ArrayList<>();
		this.userDefinedParameters = new HashMap<>();
		this.maxCount = 0;
	}

	public String getPerformative()
	{
		return performative;
	}

	public ACLMessageQuery setPerformative(String performative)
	{
		this.performative = performative;
		return this;
	}

	public AgentIdentifier getSender()
	{
		return sender;
	}

	public ACLMessageQuery setSender(AgentIdentifier sender)
	{
		this.sender = sender;
		return this;
	}

	public String getReply_with()
	{
		return reply_with;
	}

	public ACLMessageQuery setReply_with(String reply_with)
	{
		this.reply_with = reply_with;
		return this;
	}

	public Date getReply_by()
	{
		return reply_by;
	}

	public ACLMessageQuery setReply_by(Date reply_by)
	{
		this.reply_by = reply_by;
		return this;
	}

	public String getReply_to()
	{
		return reply_to;
	}

	public ACLMessageQuery setReply_to(String reply_to)
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

	public ACLMessageQuery setLanguage(String language)
	{
		this.language = language;
		return this;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public ACLMessageQuery setEncoding(String encoding)
	{
		this.encoding = encoding;
		return this;
	}

	public String getOntology()
	{
		return ontology;
	}

	public ACLMessageQuery setOntology(String ontology)
	{
		this.ontology = ontology;
		return this;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public ACLMessageQuery setProtocol(String protocol)
	{
		this.protocol = protocol;
		return this;
	}

	public String getConversation_id()
	{
		return conversation_id;
	}

	public ACLMessageQuery setConversation_id(String conversation_id)
	{
		this.conversation_id = conversation_id;
		return this;
	}
	
	public Map<String, String> getUserDefinedParameters()
	{
		return userDefinedParameters;
	}

	public int getMaxCount()
	{
		return maxCount;
	}

	public ACLMessageQuery setMaxCount(int maxCount)
	{
		this.maxCount = maxCount;
		return this;
	}
}
