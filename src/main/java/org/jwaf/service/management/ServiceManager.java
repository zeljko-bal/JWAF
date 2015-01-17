package org.jwaf.service.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.jwaf.platform.annotation.resource.EJBJNDIPrefix;
import org.jwaf.service.AgentService;

@Stateless
@LocalBean
public class ServiceManager
{
	@Inject @EJBJNDIPrefix
	private String ejbJNDIPrefix;
	
	@Inject
	private Instance<AgentService> allServices;
	
	@Inject
	private BeanManager beanManager;
	
	public List<String> find(Map<String, String> attributes)
	{
		List<String> ret = new ArrayList<String>();
		
		allServices.forEach(service -> 
		{
			if(attributesMatch(attributes, service))
			{
				ret.add(service.getClass().getName());
			}
		});
		
		return ret;
	}
	
	private boolean attributesMatch(Map<String, String> attributes, AgentService service)
	{
		Map<String, String> serviceAttributes = getAttributes(service);
		
		for(String key : attributes.keySet())
		{
			if(!serviceAttributes.get(key).equals(attributes.get(key)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean exists(String name)
	{
		return findService(name) != null;
	}
	
	public Map<String, String> getAttributes(AgentService service)
	{
		Set<Bean<?>> beans = beanManager.getBeans(service.getClass());
		
		if(beans.size() != 1)
		{
			// TODO throw service not found/multiple
			return null;
		}
		
		Bean<?> serviceBean = beans.iterator().next();
		Class<?> serviceClass = serviceBean.getBeanClass();
		
		if(serviceClass.isAnnotationPresent(TypeAttributes.class))
		{
			Map<String, String> ret = new HashMap<>();
			
			for(TypeAttribute attribute : serviceClass.getAnnotation(TypeAttributes.class).value())
			{
				ret.put(attribute.key(), attribute.value());
			}
			
			return ret;
		}
		else
		{
			return new HashMap<>();
		}
	}
	
	public Map<String, String> getAttributes(String name)
	{
		return getAttributes(findService(name));
	}
	
	public Object callSynch(String serviceName, Object... params)
	{
		AgentService service = findService(serviceName);
		return service.call(params);
	}
	
	@Asynchronous
	public Future<Object> callAsynch(String serviceName, Object... params)
	{
		Object result = callSynch(serviceName, params);
		return new AsyncResult<Object>(result);
	}
	
	private AgentService findService(String name)
	{
		try
		{
			return (AgentService)(new InitialContext()).lookup(ejbJNDIPrefix + name);
		}
		catch (NamingException | ClassCastException e)
		{
			return null;
		}
	}
}
