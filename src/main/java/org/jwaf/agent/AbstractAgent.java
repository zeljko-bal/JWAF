package org.jwaf.agent;

import javax.inject.Inject;

import org.jwaf.agent.annotation.AgentQualifier;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.services.AgentServices;
import org.jwaf.agent.services.MessageServices;
import org.jwaf.agent.services.ServiceDirectory;
import org.jwaf.platform.LocalPlatform;

@AgentQualifier
public abstract class AbstractAgent
{
	protected AgentIdentifier aid;
	
	@Inject 
	protected LocalPlatform localPlatform;
	
	@Inject
	protected MessageServices messageServices;
	
	@Inject
	protected AgentServices agentServices;
	
	@Inject
	protected ServiceDirectory serviceDirectory;
	
	public void setAid(AgentIdentifier aid) 
	{
		this.aid = aid;
		messageServices.setAid(aid);
		agentServices.setAid(aid);
	}

	public abstract void execute();
	
	public void setup()
	{/* no-op */}
}
