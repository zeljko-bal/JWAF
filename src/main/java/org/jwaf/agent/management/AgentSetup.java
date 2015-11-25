package org.jwaf.agent.management;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jwaf.agent.MultiThreadedAgent;
import org.jwaf.agent.SingleThreadedAgent;
import org.jwaf.agent.annotations.AgentQualifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.slf4j.Logger;

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
	@SuppressWarnings("serial")
	private void setup()
	{
		Set<Bean<?>> agentBeans = beanManager.getBeans(Object.class, new AnnotationLiteral<AgentQualifier>() {});
		// TODO try Agent.class instead of Object.class, no annotations..
		
		agentBeans.forEach(this::registerType);
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
		
		if(!(implementsInterface(agentClass, SingleThreadedAgent.class) ||
			 implementsInterface(agentClass, MultiThreadedAgent.class)))
		{
			log.error("Agent type: <{}> must implement either SingleThreadedAgent or MultiThreadedAgent.", typeName);
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
	}
	
	private boolean implementsInterface(Class<?> agentClass, Class<?> inter)
	{
		for(Class<?> implemented : agentClass.getInterfaces())
		{
			if(inter.equals(implemented)) return true;
		}
		return false;
	}
}
