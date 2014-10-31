package org.jwaf.platform;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;

import org.jwaf.platform.annotation.resource.AgentJNDIPrefix;
import org.jwaf.platform.annotation.resource.LocalPlatformAddress;
import org.jwaf.platform.annotation.resource.LocalPlatformName;

@Singleton
@LocalBean
@Startup
public class LocalPlatformPtoperties
{
    @Resource(name = "platform_name")
	private String name;

    @Resource(name = "platform_address")
    private String addressString;
    
	private URL address;
	
	@Resource(name = "agent_jndi_prefix")
	private String agentJNDIPrefix;
	
	@PostConstruct
	public void setup()
	{
		try
		{
			address = new URL("http://localhost:8080/jwaf/"/* TODO addressString*/);
		} 
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	@Produces @LocalPlatformName
	public String getName()
	{
		return name;
	}
	
	@Produces @LocalPlatformAddress
	public URL getAddress()
	{
		return address;
	}
	
	@Produces @AgentJNDIPrefix
	public String getAgentJNDIPrefix()
	{
		return agentJNDIPrefix;
	}
}
