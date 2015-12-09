package org.jwaf.platform.management;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jwaf.common.util.PropertiesUtils;
import org.jwaf.platform.annotations.resource.EJBJNDIPrefix;
import org.jwaf.platform.annotations.resource.LocalPlatformAddress;
import org.jwaf.platform.annotations.resource.LocalPlatformName;
import org.slf4j.Logger;

@Singleton
@LocalBean
@Startup
public class LocalPlatformPtoperties
{
	private String name;
	private URL address;
	private String ejbJNDIPrefix;
	
	@Inject
	private Logger logger;
	
	@PostConstruct
	public void setup()
	{
		try
		{
			Properties properties = PropertiesUtils.getProperties("platform.properties");
			
			name = properties.getProperty("platform_name");
			address = new URL(properties.getProperty("platform_address"));
			ejbJNDIPrefix = properties.getProperty("agent_jndi_prefix");
		}
		catch (IOException e)
		{
			logger.error("Error while loading platform.properties", e);
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
	
	@Produces @EJBJNDIPrefix
	public String getEJBJNDIPrefix()
	{
		return ejbJNDIPrefix;
	}
}
