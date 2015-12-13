package org.jwaf.base;

import java.net.URL;

import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.base.tools.AgentDirectory;
import org.jwaf.base.tools.AgentTools;
import org.jwaf.base.tools.AutoPersister;
import org.jwaf.base.tools.DataTools;
import org.jwaf.base.tools.EventTools;
import org.jwaf.base.tools.MessageTools;
import org.jwaf.base.tools.RemotePlatformTools;
import org.jwaf.base.tools.ServiceDirectory;
import org.jwaf.base.tools.TaskTools;
import org.jwaf.base.tools.TimerTools;
import org.jwaf.base.tools.TypeTools;
import org.jwaf.data.management.AgentDataManager;
import org.jwaf.event.management.EventManager;
import org.jwaf.event.management.TimerManager;
import org.jwaf.message.management.MessageSender;
import org.jwaf.platform.annotations.resource.LocalPlatformAddress;
import org.jwaf.platform.annotations.resource.LocalPlatformName;
import org.jwaf.remote.management.RemotePlatformManager;
import org.jwaf.service.management.ServiceManager;
import org.jwaf.task.management.TaskManager;

public abstract class BaseAgent extends EmptyBaseAgent implements SerializableAgent
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
	
	@Inject @LocalPlatformAddress
	protected URL localPlatformAddress;
	
	/*
	 * Agent tools
	 */
	
	protected AgentDirectory agentDirectory;
	protected MessageTools messages;
	protected AgentTools self;
	protected TaskTools tasks;
	protected EventTools events;
	protected TimerTools timers;
	protected RemotePlatformTools remotePlatforms;
	protected ServiceDirectory services;
	protected TypeTools types;
	protected DataTools data;
	protected AutoPersister autoDataPersister;
	
	@Override
	protected void postConstruct()
	{
		super.postConstruct();
		remotePlatforms = new RemotePlatformTools(this, remoteManager);
		agentDirectory = new AgentDirectory(aidManager, agentManager, agentDataManager, remoteManager, localPlatformName);
		messages = new MessageTools(messageSender, agentManager);
		self = new AgentTools(agentManager, aidManager);
		tasks = new TaskTools(taskManager);
		events = new EventTools(eventManager);
		timers = new TimerTools(timerManager);
		services = new ServiceDirectory(serviceManager);
		types = new TypeTools(agentManager, typeManager);
		data = new DataTools(agentDataManager);
		autoDataPersister = new AutoPersister(this, data, true);
	}
	
	@Override
	protected void onSetAid(AgentIdentifier aid)
	{
		super.onSetAid(aid);
		messages.setAid(aid);
		self.setAid(aid);
		tasks.setAid(aid);
		events.setAid(aid);
		remotePlatforms.setAid(aid);
		services.setAid(aid);
		data.setAid(aid);
	}
	
	@Override
	public String serialize()
	{
		return data.getAllDataAsString();
	}
	
	@Override
	protected void preArrival(String jsonData)
	{
		data.initializeData(jsonData);
		autoDataPersister.autoLoad();
	}
	
	@Override
	protected void postArrival(String jsonData)
	{
		autoDataPersister.autoPersist();
	}
	
	@Override
	protected void preExecute()
	{
		autoDataPersister.autoLoad();
	}
	
	@Override
	protected void postExecute()
	{
		autoDataPersister.autoPersist();
	}
}
