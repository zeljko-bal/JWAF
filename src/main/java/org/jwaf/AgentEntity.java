package org.jwaf;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;



@Entity
public class AgentEntity
{
	@Id @GeneratedValue
	private int id;
	
	@OneToOne
	private AgentIdentifier aid;
	
	@ManyToOne
	private AgentType type;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, Serializable> privateData;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, Serializable> publicData;
	

}
