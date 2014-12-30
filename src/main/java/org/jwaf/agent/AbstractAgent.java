package org.jwaf.agent;

import javax.inject.Inject;

import org.jwaf.agent.annotation.AgentQualifier;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.services.AgentDirectory;
import org.jwaf.agent.services.AgentServices;
import org.jwaf.agent.services.EventServices;
import org.jwaf.agent.services.MessageServices;
import org.jwaf.agent.services.RemotePlatformServices;
import org.jwaf.agent.services.ServiceDirectory;
import org.jwaf.agent.services.TaskServices;
import org.jwaf.agent.services.TypeServices;

@AgentQualifier
public abstract class AbstractAgent
{
	protected AgentIdentifier aid;
	
	@Inject
	protected AgentDirectory agentDirectory;
	
	@Inject
	protected MessageServices messageServices;
	
	@Inject
	protected AgentServices agentServices;
	
	@Inject
	protected TaskServices taskServices;
	
	@Inject
	protected EventServices eventServices;
	
	@Inject
	protected RemotePlatformServices remoteServices;
	
	@Inject
	protected ServiceDirectory serviceDirectory;
	
	@Inject
	protected TypeServices typeServices;
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
		messageServices.setAid(aid);
		agentServices.setAid(aid);
		taskServices.setAid(aid);
		eventServices.setAid(aid);
	}

	public abstract void execute();
	
	public void setup()
	{/* no-op */}
	
	public void onArrival()
	{/* no-op */}
}
