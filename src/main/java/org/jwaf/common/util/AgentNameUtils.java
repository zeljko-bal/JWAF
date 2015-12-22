package org.jwaf.common.util;

import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.platform.annotations.resource.LocalPlatformName;

@Stateless
@LocalBean
public class AgentNameUtils
{
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	/**
	 * Creates a name for an agent in the following format:
	 * <random-uuid>:<agent-type>@<local-platform-name>
	 * 
	 * @param type agent type name
	 * @return new random agent name
	 */
	public String createRandom(String type)
	{
		return UUID.randomUUID().toString()+":"+type+"@"+localPlatformName;
	}
	
	/**
	 * Creates a name for an agent in the following format:
	 * <provided-name>:<agent-type>@<local-platform-name>
	 * 
	 * @param type agent type name
	 * @param name provided nane to be used in an agent name
	 * @return an agent name based on the provided name string
	 */
	public String createNamed(String type, String name)
	{
		return name+":"+type+"@"+localPlatformName;
	}
}
