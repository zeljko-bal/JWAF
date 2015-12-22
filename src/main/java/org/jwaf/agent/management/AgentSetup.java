package org.jwaf.agent.management;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jwaf.agent.Agent;
import org.jwaf.agent.MultiThreadedAgent;
import org.jwaf.agent.SingleThreadedAgent;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.common.annotations.attributes.TypeAttributes;
import org.slf4j.Logger;

/**
 * Setup bean that registers agent types.
 * 
 * @author zeljko.bal
 */
@Singleton
@LocalBean
@Startup
public class AgentSetup
{
	@Inject
	private BeanManager beanManager;
	
	@Inject
	private AgentTypeManager agentTypeManager;
	
	@Inject
	Logger log;
	
	@PostConstruct
	private void setup()
	{
		beanManager.getBeans(Object.class)
				.stream()
				.filter(b->Agent.class.isAssignableFrom(b.getBeanClass()))
				.forEach(this::registerType);
	}
	
	private void registerType(Bean<?> agentBean)
	{
		Class<?> agentClass = agentBean.getBeanClass();
		
		String typeName = agentClass.getSimpleName();
		
		if(agentTypeManager.find(typeName) != null)
		{
			log.info("Agent type: <{}> already registered.", typeName);
			return;
		}
		
		if(!(SingleThreadedAgent.class.isAssignableFrom(agentClass) ||
			 MultiThreadedAgent.class.isAssignableFrom(agentClass)))
		{
			log.error("Agent type: <{}> must implement either SingleThreadedAgent or MultiThreadedAgent.", typeName);
			return;
		}
		
		AgentType type = new AgentType(typeName);
		
		// extract type parameters info
		if(agentClass.isAnnotationPresent(TypeAttributes.class))
		{
			for(TypeAttribute attribute : agentClass.getAnnotation(TypeAttributes.class).value())
			{
				type.getAttributes().put(attribute.key(), attribute.value());
			}
		}
		else if(agentClass.isAnnotationPresent(TypeAttribute.class))
		{
			TypeAttribute attr = agentClass.getAnnotation(TypeAttribute.class);
			type.getAttributes().put(attr.key(), attr.value());
		}
		
		agentTypeManager.create(type);
		
		log.info("Registered agent type: <{}>.", type.getName());
	}
}
