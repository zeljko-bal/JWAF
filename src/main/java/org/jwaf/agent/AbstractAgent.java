package org.jwaf.agent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jwaf.agent.annotations.AgentQualifier;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.management.AidManager;
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
import org.jwaf.event.management.EventManager;
import org.jwaf.event.management.TimerManager;
import org.jwaf.message.management.MessageSender;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.service.management.ServiceManager;
import org.jwaf.task.manager.TaskManager;

@AgentQualifier
public abstract class AbstractAgent
{
	/*
	 * Injected resources
	 */
	
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private AidManager aidManager;
	
	@Inject
	private EventManager eventManager;
	
	@Inject
	private MessageSender messageSender;
	
	@Inject
	private RemotePlatformManager remoteManager;
	
	@Inject
	private ServiceManager serviceManager;
	
	@Inject
	private TaskManager taskManager;
	
	@Inject
	private TimerManager timerManager;
	
	@Inject
	private AgentTypeManager typeManager;
	
	@Inject @LocalPlatformName
	protected String localPlatformName;
	
	/*
	 * Agent services
	 */
	
	protected AgentIdentifier aid;
	protected AgentDirectory agent;
	protected MessageServices message;
	protected AgentServices self;
	protected TaskServices task;
	protected EventServices event;
	protected TimerServices timer;
	protected RemotePlatformServices remotePlatforms;
	protected ServiceDirectory service;
	protected TypeServices type;
	
	@PostConstruct
	protected void postConstruct()
	{
		remotePlatforms = new RemotePlatformServices(remoteManager);
		agent = new AgentDirectory(aidManager, agentManager, remotePlatforms, localPlatformName);
		message = new MessageServices(messageSender, agentManager);
		self = new AgentServices(agentManager);
		task = new TaskServices(taskManager);
		event = new EventServices(eventManager);
		timer = new TimerServices(timerManager);
		service = new ServiceDirectory(serviceManager);
		type = new TypeServices(agentManager, typeManager);
	}
	
	private void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
		message.setAid(aid);
		self.setAid(aid);
		task.setAid(aid);
		event.setAid(aid);
		remotePlatforms.setAid(aid);
	}
	
	public void _execute(AgentIdentifier aid) throws Exception
	{
		setAid(aid);
		execute();
	}
	
	public void _setup(AgentIdentifier aid)
	{
		setAid(aid);
		setup();
	}
	
	public void _onArrival(AgentIdentifier aid)
	{
		setAid(aid);
		onArrival();
	}

	public abstract void execute() throws Exception;
	
	public void setup()
	{/* no-op */}
	
	public void onArrival()
	{/* no-op */}
}
