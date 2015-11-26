package org.jwaf.service.management;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.platform.annotations.resource.EJBJNDIPrefix;
import org.jwaf.service.AgentService;
import org.jwaf.service.persistence.entity.AgentServiceType;
import org.jwaf.service.persistence.repository.AgentServiceRepository;

@Stateless
@LocalBean
public class ServiceManager
{
	@Inject
	private AgentServiceRepository serviceRepo;
	
	@Inject @EJBJNDIPrefix
	private String ejbJNDIPrefix;
	
	public List<String> find(Map<String, String> attributes)
	{
		return serviceRepo.find(attributes);
	}
	
	public boolean exists(String name)
	{
		return findService(name) != null;
	}
	
	public Map<String, String> getAttributes(String name)
	{
		return serviceRepo.find(name).getAttributes();
	}
	
	public Object callSync(String serviceName, Object... params)
	{
		AgentService service = findService(serviceName);
		return service.call(params);
	}
	
	@Asynchronous
	public Future<Object> callAsync(String serviceName, Object... params)
	{
		Object result = callSync(serviceName, params);
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

	public void register(AgentServiceType service)
	{
		serviceRepo.register(service);
	}
}
