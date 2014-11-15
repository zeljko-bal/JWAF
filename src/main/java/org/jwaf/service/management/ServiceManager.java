package org.jwaf.service.management;

import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.platform.annotation.resource.EJBJNDIPrefix;
import org.jwaf.service.AgentService;

@Stateless
@LocalBean
public class ServiceManager
{
	@Inject @EJBJNDIPrefix
	private String ejbJNDIPrefix;
	
	public boolean exists(String name)
	{
		return findService(name) != null;
	}
	
	public Map<String, String> getAttributes(String name)
	{
		return findService(name).getAttributes();
	}
	
	public Object callSynch(AgentIdentifier aid, String serviceName, Object param)
	{
		AgentService service = findService(serviceName);
		return service.call(param);
	}
	
	@Asynchronous
	public Future<Object> callAsynch(AgentIdentifier aid, String serviceName, Object param)
	{
		AgentService service = findService(serviceName);
		Object result = service.call(param);
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
