package org.jwaf.platform;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.platform.annotation.resource.LocalPlatformAddress;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.task.management.TaskManager;
import org.slf4j.Logger;


@Singleton
@LocalBean
@Startup
@DependsOn({"LocalPlatformPtoperties", "AgentSetup"})
public class LocalPlatformSetup 
{
	@Inject
	private AidManager aidManager;
	
	@Inject
	private AgentManager agentManager;
	
	@Inject
	TaskManager taskManager;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
	@Inject
	Logger log;

	@PostConstruct
	private void setup()
	{
		try
		{
			createLocalPlatformAid();
			
			createCleanupAgent();
		}
		catch(Exception e)
		{
			log.error("Error during platform setup.", e);
		}
	}

	private void createCleanupAgent()
	{
		CreateAgentRequest createCleanupRequest = new CreateAgentRequest("CleanUpAgent");
		createCleanupRequest.getParams().put("X-cleanup-agent", "true");
		
		if(!aidManager.find(createCleanupRequest.getParams()).isEmpty())
		{
			log.info("Cleanup already exists.");
			return;
		}
		
		agentManager.initialize(createCleanupRequest);
	}

	private void createLocalPlatformAid()
	{
		if(agentManager.contains(localPlatformName))
		{
			return;
		}
		
		AgentIdentifier platformAid = new AgentIdentifier(localPlatformName);
		platformAid.getAddresses().add(localPlatformAddress);
		aidManager.createAid(platformAid);
	}
}
