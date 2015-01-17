package org.jwaf.util;

import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.platform.annotation.resource.LocalPlatformName;

@Stateless
@LocalBean
public class AgentNameUtil
{
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	public String createRandom(String type)
	{
		return UUID.randomUUID().toString()+":"+type+"@"+localPlatformName;
	}
}
