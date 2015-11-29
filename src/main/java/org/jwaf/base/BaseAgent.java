package org.jwaf.base;

import javax.inject.Inject;

import org.jwaf.agent.annotations.AgentQualifier;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.base.tools.AgentDirectory;
import org.jwaf.base.tools.AgentTools;
import org.jwaf.base.tools.EventTools;
import org.jwaf.base.tools.MessageTools;
import org.jwaf.base.tools.PlatformTools;
import org.jwaf.base.tools.RemotePlatformTools;
import org.jwaf.base.tools.ServiceDirectory;
import org.jwaf.base.tools.TaskTools;
import org.jwaf.base.tools.TimerTools;
import org.jwaf.base.tools.TypeTools;
import org.jwaf.data.management.AgentDataManager;
import org.jwaf.event.management.EventManager;
import org.jwaf.event.management.TimerManager;
import org.jwaf.message.management.MessageSender;
import org.jwaf.platform.annotations.resource.LocalPlatformName;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.service.management.ServiceManager;
import org.jwaf.task.management.TaskManager;

@AgentQualifier
public abstract class BaseAgent extends EmptyBaseAgent
{
	/*
	 * Injected resources
	 */
	
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private AidManager aidManager;
	
	@Inject
	private AgentDataManager agentDataManager;
	
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
	 * Agent tools
	 */
	
	protected AgentDirectory agent;
	protected MessageTools message;
	protected AgentTools self;
	protected TaskTools task;
	protected EventTools event;
	protected TimerTools timer;
	protected RemotePlatformTools remotePlatforms;
	protected PlatformTools platform;
	protected ServiceDirectory service;
	protected TypeTools type;
	
	@Override
	protected void postConstruct()
	{
		super.postConstruct();
		remotePlatforms = new RemotePlatformTools(this, remoteManager);
		agent = new AgentDirectory(aidManager, agentManager, agentDataManager, remoteManager, localPlatformName);
		message = new MessageTools(messageSender, agentManager);
		self = new AgentTools(agentManager, aidManager, agentDataManager);
		task = new TaskTools(taskManager);
		event = new EventTools(eventManager);
		timer = new TimerTools(timerManager);
		service = new ServiceDirectory(serviceManager);
		type = new TypeTools(agentManager, typeManager);
		platform = new PlatformTools(aidManager);
	}

	@Override
	protected void onSetAid(AgentIdentifier aid)
	{
		super.onSetAid(aid);
		message.setAid(aid);
		self.setAid(aid);
		task.setAid(aid);
		event.setAid(aid);
		remotePlatforms.setAid(aid);
		service.setAid(aid);
	}
	
	@Override
	public String serialize()
	{
		return agentDataManager.getAllDataAsString(aid.getName());
	}
	
	@Override
	protected void onArrival(String data)
	{
		agentDataManager.initializeData(aid.getName(), data);
	}
}
