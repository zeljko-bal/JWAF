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
import org.jwaf.agent.services.TimerServices;
import org.jwaf.agent.services.TypeServices;

@AgentQualifier
public abstract class AbstractAgent
{
	protected AgentIdentifier aid;
	
	@Inject
	protected AgentDirectory agent;
	
	@Inject
	protected MessageServices message;
	
	@Inject
	protected AgentServices self;
	
	@Inject
	protected TaskServices task;
	
	@Inject
	protected EventServices event;
	
	@Inject
	protected TimerServices timer;
	
	@Inject
	protected RemotePlatformServices remotePlatforms;
	
	@Inject
	protected ServiceDirectory service;
	
	@Inject
	protected TypeServices type;
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
		message.setAid(aid);
		self.setAid(aid);
		task.setAid(aid);
		event.setAid(aid);
	}

	public abstract void execute();
	
	public void setup()
	{/* no-op */}
	
	public void onArrival()
	{/* no-op */}
}
