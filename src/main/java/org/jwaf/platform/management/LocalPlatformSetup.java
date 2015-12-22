package org.jwaf.platform.management;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jwaf.agent.Agent;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.platform.annotations.SystemAgent;
import org.slf4j.Logger;

/**
 * A setup bean that registers system agents. 
 * System agents must be annotated with {@link SystemAgent} which should contain 
 * a unique name for that system agent.
 * 
 * @author zeljko.bal
 */
@Singleton
@LocalBean
@Startup
@DependsOn("AgentSetup")
public class LocalPlatformSetup 
{
	@Inject
	private AidManager aidManager;
	
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private BeanManager beanManager;
	
	@Inject
	Logger log;

	@PostConstruct
	private void setup()
	{
		try
		{
			registerSystemAgents();
		}
		catch(Exception e)
		{
			log.error("Error during platform setup.", e);
		}
	}
	
	private void registerSystemAgents()
	{
		beanManager.getBeans(Object.class)
				.stream()
				.filter(b->b.getBeanClass().isAnnotationPresent(SystemAgent.class) &&
							Agent.class.isAssignableFrom(b.getBeanClass()))
				.forEach(this::registerSystemAgent);
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
