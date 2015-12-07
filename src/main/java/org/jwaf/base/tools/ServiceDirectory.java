package org.jwaf.base.tools;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.mongo.QueryFunction;
import org.jwaf.service.management.ServiceManager;
import org.jwaf.service.persistence.entity.AgentServiceType;

public class ServiceDirectory
{
	private ServiceManager serviceManager;
	private AgentIdentifier aid;
	
	public ServiceDirectory(ServiceManager serviceManager)
	{
		this.serviceManager = serviceManager;
	}
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}

	public List<String> find(Map<String, String> attributes)
	{
		return serviceManager.find(attributes);
	}
	
	public List<String> find(QueryFunction<AgentServiceType> queryFunc)
	{
		return serviceManager.find(queryFunc);
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
		return serviceManager.callSync(serviceName, params);
	}
	
	public Future<Object> callAsynch(String serviceName, Object... params)
	{
		return serviceManager.callAsync(serviceName, params);
	}
	
	public Future<Object> callForMessage(String serviceName, Object... params)
	{
		Object[] serviceParams = new Object[params.length+1];
		serviceParams[0] = aid.getName();
		for(int i=1; i < serviceParams.length; i++)
		{
			serviceParams[i] = params[i-1];
		}
		
		return serviceManager.callAsync(serviceName, serviceParams);
	}
}
