package org.jwaf.agent.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.service.management.ServiceManager;

@Stateless
@LocalBean
public class ServiceDirectory
{
	@Inject
	private ServiceManager serviceManager;
	
	public List<String> find(Map<String, String> attributes)
	{
		// TODO find service by attributes
		return null;
	}
	
	public boolean exists(String name)
	{
		return serviceManager.exists(name);
	}
	
	public Map<String, String> getAttributes(String name)
	{
		return serviceManager.getAttributes(name);
	}
	
	public Object callSynch(AgentIdentifier aid, String serviceName, Object param)
	{
		return serviceManager.callSynch(aid, serviceName, param);
	}
	
	public Future<Object> callAsynch(AgentIdentifier aid, String serviceName, Object param)
	{
		return serviceManager.callAsynch(aid, serviceName, param);
	}
}
