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
	private void setup()
	{
		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<AgentQualifier>() {});
		
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
}
