package org.jwaf.agent.services;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Stateless
@LocalBean
public class ServiceDirectory
{
	//private AgentIdentifier aid;
	
	public void setAid(AgentIdentifier aid)
	{
		//this.aid = aid;
	}
}
