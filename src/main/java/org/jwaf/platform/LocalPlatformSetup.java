package org.jwaf.platform;

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

import org.jwaf.agent.AbstractAgent;
import org.jwaf.agent.annotations.AgentQualifier;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.jwaf.platform.annotation.resource.LocalPlatformAddress;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.service.AgentService;
import org.jwaf.service.annotations.ServiceQualifier;
import org.jwaf.service.management.ServiceManager;
import org.jwaf.service.persistence.entity.AgentServiceType;
import org.jwaf.task.manager.TaskManager;
import org.slf4j.Logger;


@Singleton
@LocalBean
@Startup
@DependsOn("LocalPlatformPtoperties")
public class LocalPlatformSetup 
{
	@Inject
	private AgentTypeManager agentTypeManager;
	
	@Inject
	private ServiceManager serviceManager;
	
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
			registerAgentTypes();
			
			registerServiceTypes();
			
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
		platformAid.getUserDefinedParameters().put("X-agent-platform", "true");
		aidManager.createAid(platformAid);
	}

	private void registerAgentTypes()
	{
		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(AbstractAgent.class, new AnnotationLiteral<AgentQualifier>() {});
		
		beans.forEach(agentBean ->
		{
			Class<?> agentClass = agentBean.getBeanClass();
			
			String typeName = agentClass.getSimpleName();
			
			if(agentTypeManager.find(typeName) != null)
			{
				log.info("Agent type: <{}> already registered.", typeName);
				return;
			}
			
			AgentType type = new AgentType(typeName);

			if(agentClass.isAnnotationPresent(TypeAttributes.class))
			{
				for(TypeAttribute attribute : agentClass.getAnnotation(TypeAttributes.class).value())
				{
					type.getAttributes().put(attribute.key(), attribute.value());
				}
			}
			
			agentTypeManager.create(type);
			
			log.info("Registered agent type: <{}>.", type.getName());
		});
	}
	
	private void registerServiceTypes()
	{
		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(AgentService.class, new AnnotationLiteral<ServiceQualifier>() {});
		
		beans.forEach(serviceBean ->
		{
			Class<?> serviceClass = serviceBean.getBeanClass();
			
			String serviceName = serviceClass.getSimpleName();
			
			AgentServiceType type = new AgentServiceType(serviceName);

			if(serviceClass.isAnnotationPresent(TypeAttributes.class))
			{
				for(TypeAttribute attribute : serviceClass.getAnnotation(TypeAttributes.class).value())
				{
					type.getAttributes().put(attribute.key(), attribute.value());
				}
			}

			serviceManager.register(type);
			
			log.info("Registered service type: <{}>.", type.getName());
		});
	}
}
