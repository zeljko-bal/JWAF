package org.jwaf.util;

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
	
	public String createRandom(String type)
	{
		return UUID.randomUUID().toString()+":"+type+"@"+localPlatformName;
	}
	
	public String createNamed(String type, String name)
	{
		return name+":"+type+"@"+localPlatformName;
	}
}
