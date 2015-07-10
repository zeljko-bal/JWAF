package org.jwaf.agent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jwaf.agent.annotations.AgentQualifier;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.tools.AgentDirectory;
import org.jwaf.agent.tools.AgentLogger;
import org.jwaf.agent.tools.AgentTools;
import org.jwaf.agent.tools.EventTools;
import org.jwaf.agent.tools.MessageTools;
import org.jwaf.agent.tools.RemotePlatformTools;
import org.jwaf.agent.tools.ServiceDirectory;
import org.jwaf.agent.tools.TaskTools;
import org.jwaf.agent.tools.TimerTools;
import org.jwaf.agent.tools.TypeTools;
import org.jwaf.event.management.EventManager;
import org.jwaf.event.management.TimerManager;
import org.jwaf.message.management.MessageSender;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.service.management.ServiceManager;
import org.jwaf.task.manager.TaskManager;
import org.jwaf.util.annotations.NamedLogger;
import org.slf4j.Logger;

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
	
	@Inject @NamedLogger("AGENT")
	private Logger logger;
	
	/*
	 * Agent services
	 */
	
	protected AgentIdentifier aid;
	protected AgentDirectory agent;
	protected MessageTools message;
	protected AgentTools self;
	protected TaskTools task;
	protected EventTools event;
	protected TimerTools timer;
	protected RemotePlatformTools remotePlatforms;
	protected ServiceDirectory service;
	protected TypeTools type;
	protected AgentLogger log;
	
	@PostConstruct
	protected void postConstruct()
	{
		remotePlatforms = new RemotePlatformTools(remoteManager);
		agent = new AgentDirectory(aidManager, agentManager, remotePlatforms, localPlatformName);
		message = new MessageTools(messageSender, agentManager);
		self = new AgentTools(agentManager);
		task = new TaskTools(taskManager);
		event = new EventTools(eventManager);
		timer = new TimerTools(timerManager);
		service = new ServiceDirectory(serviceManager);
		type = new TypeTools(agentManager, typeManager);
		log = new AgentLogger(logger);
	}
	
	private void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
		message.setAid(aid);
		self.setAid(aid);
		task.setAid(aid);
		event.setAid(aid);
		remotePlatforms.setAid(aid);
		log.setAid(aid);
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
