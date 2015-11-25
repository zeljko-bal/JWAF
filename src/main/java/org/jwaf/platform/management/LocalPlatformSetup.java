package org.jwaf.platform.management;

import java.net.URL;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.platform.annotations.SystemAgent;
import org.jwaf.platform.annotations.resource.LocalPlatformAddress;
import org.jwaf.platform.annotations.resource.LocalPlatformName;
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
	
	@Inject
	private BeanManager beanManager;
	
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
			
			registerSystemAgents();
		}
		catch(Exception e)
		{
			log.error("Error during platform setup.", e);
		}
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
	
	private void registerSystemAgents()
	{
		@SuppressWarnings("serial")
		Set<Bean<?>> agentBeans = beanManager.getBeans(Object.class, new AnnotationLiteral<SystemAgent>() {});
		agentBeans.forEach(this::registerSystemAgent);
	}
	
	private void registerSystemAgent(Bean<?> agentBean)
	{
		Class<?> agentClass = agentBean.getBeanClass();
		
		String name = agentClass.getAnnotation(SystemAgent.class).value();
		
		if(aidManager.find(name) == null)
		{
			CreateAgentRequest request = new CreateAgentRequest(agentClass.getSimpleName())
				.param(CreateAgentRequest.AID_NAME, name);
			
			agentManager.initialize(request);
		}
		else
		{
			log.info("<{}> system agent already registered.", name);
		}
	}
}
