package org.jwaf.agent.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.jwaf.service.management.ServiceManager;

public class ServiceDirectory
{
	private ServiceManager serviceManager;
	
	public ServiceDirectory(ServiceManager serviceManager)
	{
		this.serviceManager = serviceManager;
	}

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
