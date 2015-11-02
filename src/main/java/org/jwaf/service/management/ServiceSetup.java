package org.jwaf.service.management;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.jwaf.service.AgentService;
import org.jwaf.service.annotations.ServiceQualifier;
import org.jwaf.service.persistence.entity.AgentServiceType;
import org.slf4j.Logger;

@Singleton
@LocalBean
@Startup
public class ServiceSetup
{
	@Inject
	private BeanManager beanManager;
	
	@Inject
	private ServiceManager serviceManager;
	
	@Inject
	Logger log;
	
	@PostConstruct
	private void setup()
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
