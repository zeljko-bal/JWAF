package org.jwaf.agent;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.entity.AgentIdentifier;

@Stateless
@LocalBean
public abstract class AgentBean
{
	AgentIdentifier aid;
	
	public void setAid(AgentIdentifier aid) 
	{
		this.aid = aid;
	}

	public abstract void execute();

}
