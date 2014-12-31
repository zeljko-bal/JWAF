package org.jwaf.platform;

import java.net.URL;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jwaf.agent.AbstractAgent;
import org.jwaf.agent.annotation.AgentQualifier;
import org.jwaf.agent.annotation.TypeAttribute;
import org.jwaf.agent.annotation.TypeAttributes;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
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

	/*@Inject
	private MessageManager messageManager;
	
	@Inject
	private AgentRepository agentRepo;*/
	
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
			
			// create local platform aid
			AgentIdentifier platformAid = new AgentIdentifier(localPlatformName);
			platformAid.getAddresses().add(localPlatformAddress);
			platformAid.getUserDefinedParameters().put("X-agent-platform", "true");
			aidManager.createAid(platformAid);
			
			// create cleanup agent
			CreateAgentRequest createCleanupRequest = new CreateAgentRequest("CleanUpAgent");
			createCleanupRequest.getParams().put("X-cleanup-agent", "true");
			agentManager.initialize(createCleanupRequest);
			
			//doInitialTests();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void registerAgentTypes()
	{
		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(AbstractAgent.class, new AnnotationLiteral<AgentQualifier>() {});
		
		beans.forEach((Bean<?> agentBean)->
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
	
//	private void doInitialTests() throws NotSupportedException, SystemException
//	{
//		//////////
//
//		System.out.println( "\n\nHello World!\n\n" );
//
//		AgentType type = new AgentType("type1");
//		em.persist(type);
//
//		AgentType testType = em.createQuery("SELECT a FROM AgentType a WHERE a.name LIKE :name", AgentType.class).setParameter("name", "IntegrationTestAgent").getSingleResult();
//
//		AgentIdentifier aid1 = new AgentIdentifier("agent1@platform1");
//		em.persist(aid1);
//		AgentIdentifier aid2 = new AgentIdentifier("agent2@platform1");
//		em.persist(aid2);
//		AgentIdentifier aid3 = new AgentIdentifier("agent3@platform1");
//		em.persist(aid3);
//
//		AgentIdentifier testAid = new AgentIdentifier("test1@platform1");
//		em.persist(testAid);
//
//
//		AgentEntity agent1 = new AgentEntity(type, aid1);
//		em.persist(agent1);
//
//		AgentEntity agent2 = new AgentEntity(type, aid2);
//		em.persist(agent2);
//
//		AgentEntity testAgent = new AgentEntity(testType, testAid);
//		em.persist(testAgent);
//
//		//////////////////////////
//		/////////////////////////
//		/*
//		ACLMessage message1 = new ACLMessage();
//		message1.getReceiverList().add(aid1);
//		message1.getReceiverList().add(aid2);
//		message1.getReceiverList().add(new AgentIdentifier(aid3.getName()));
//		message1.getReceiverList().add(new AgentIdentifier("agent4@platform2"));
//
//		//em.persist(message1);
//		messageManager.handleMessage(message1);
//		 */
//		
//
//
//		/*
//		utx.begin();
//
//		AgentType type = new AgentType("type1");
//		em.persist(type);
//
//
//		AgentIdentifier aid1 = new AgentIdentifier("agent1@platform1");
//		em.persist(aid1);
//
//		utx.commit();
//		utx.begin();
//
//		AgentEntity agent = new AgentEntity(type, aid1);
//
//		agent.setState(AgentState.PASSIVE);
//
//		em.persist(agent);
//
//		utx.commit();
//		utx.begin();
//
//		ACLMessage message = new ACLMessage();
//
//		message.getReceiverList().add(aid1);
//
//		messageManager.handleMessage(message);
//
//		em.flush();
//
//		AgentIdentifier aid2 = new AgentIdentifier("agent2@platform1");
//		em.persist(aid2);
//
//		utx.commit();
//		utx.begin();
//
//		agent = new AgentEntity(type, aid2);
//
//		em.persist(agent);
//
//		utx.commit();
//		utx.begin();
//
//		message = new ACLMessage();
//
//		message.getReceiverList().add(aid1);
//		message.getReceiverList().add(new AgentIdentifier("agent3@platform2"));
//		//message.getReceiverList().add(new AgentIdentifier("agent1@platform1"));
//
//		messageManager.handleMessage(message);
//
//		utx.commit();
//		 */
//		System.out.println( "\n\nTest end!\n\n" );
//	}
}
