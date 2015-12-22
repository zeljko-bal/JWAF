package org.jwaf.service.management;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.common.annotations.attributes.TypeAttributes;
import org.jwaf.service.AgentService;
import org.jwaf.service.persistence.entity.AgentServiceType;
import org.slf4j.Logger;

/**
 * A setup bean that registers services based on beans that implement the {@link AgentService} interface.
 * 
 * @author zeljko.bal
 */
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
		beanManager.getBeans(Object.class)
				.stream()
				.filter(b->AgentService.class.isAssignableFrom(b.getBeanClass()))
				.forEach(this::registerService);
	}
	
	private void registerService(Bean<?> serviceBean)
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
		else if(serviceClass.isAnnotationPresent(TypeAttribute.class))
		{
			TypeAttribute attr = serviceClass.getAnnotation(TypeAttribute.class);
			type.getAttributes().put(attr.key(), attr.value());
		}

		serviceManager.register(type);
		
		log.info("Registered service type: <{}>.", type.getName());
	}
}
