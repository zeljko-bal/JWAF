package org.jwaf.agent.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.service.management.ServiceManager;

@Stateless
@LocalBean
public class ServiceDirectory
{
	@Inject
	private ServiceManager serviceManager;
	
	public List<String> find(Map<String, String> attributes)
	{
		return serviceManager.find(attributes);
	}
	
	public boolean exists(String name)
	{
		return serviceManager.exists(name);
	}
	
	public Map<String, String> getAttributes(String name)
	{
		return serviceManager.getAttributes(name);
	}
	
	public Object callSynch(String serviceName, Object... params)
	{
		return serviceManager.callSynch(serviceName, params);
	}
	
	public Future<Object> callAsynch(String serviceName, Object... params)
	{
		return serviceManager.callAsynch(serviceName, params);
	}
}
