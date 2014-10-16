package org.jwaf.agent.entity;

import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AgentType
{
	@Id @GeneratedValue
	private Integer id;
	
	private String type;
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Map<String, String> attributes;
}
