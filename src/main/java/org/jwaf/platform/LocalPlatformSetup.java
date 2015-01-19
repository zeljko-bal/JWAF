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
import org.jwaf.agent.annotation.AgentQualifier;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotation.resource.EJBJNDIPrefix;
import org.jwaf.platform.annotation.resource.LocalPlatformAddress;
import org.jwaf.platform.annotation.resource.LocalPlatformName;


@Singleton
@LocalBean
@Startup
@DependsOn("LocalPlatformPtoperties")
public class LocalPlatformSetup 
{
	//@PersistenceContext
	//EntityManager em;
	@Inject
	private AgentTypeManager typeManager;

	@Inject
	private MessageSender messageSender;
	
	@Inject
	private AidManager aidManager;
	
	@Inject
	private AgentManager agentManager;

	@Inject
	private BeanManager beanManager;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
	@Inject @EJBJNDIPrefix
	private String ejbJNDIPrefix;

	@PostConstruct
	private void setup()
	{
		try
		{
			registerAgentTypes();
			
			createLocalPlatformAid();
			
			createCleanupAgent();
			
			doInitialTests();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void createCleanupAgent()
	{
		CreateAgentRequest createCleanupRequest = new CreateAgentRequest("CleanUpAgent");
		createCleanupRequest.getParams().put("X-cleanup-agent", "true");
		agentManager.initialize(createCleanupRequest);
	}

	private void createLocalPlatformAid()
	{
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
			
			AgentType type = new AgentType(agentClass.getSimpleName());

			if(agentClass.isAnnotationPresent(TypeAttributes.class))
			{
				for(TypeAttribute attribute : agentClass.getAnnotation(TypeAttributes.class).value())
				{
					type.getAttributes().put(attribute.key(), attribute.value());
				}
			}

			typeManager.create(type);
			
			System.out.println("[LocalPlatformSetup] registered agent type: "+type.getName());
		});
	}
	
	private void doInitialTests()
	{
		CreateAgentRequest testCreateReq = new CreateAgentRequest("IntegrationTestAgent");
		testCreateReq.getParams().put("test_param_key_1", "test_param_value_1");
		AgentIdentifier testAid = agentManager.initialize(testCreateReq);
		
		ACLMessage testMessage = new ACLMessage("test", testAid);
		testMessage.getReceiverList().add(testAid);
		
		messageSender.send(testMessage);
	}
}
