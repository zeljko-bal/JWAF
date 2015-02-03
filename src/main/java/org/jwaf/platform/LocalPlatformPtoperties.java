package org.jwaf.platform;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;

import org.jwaf.platform.annotation.resource.EJBJNDIPrefix;
import org.jwaf.platform.annotation.resource.LocalPlatformAddress;
import org.jwaf.platform.annotation.resource.LocalPlatformName;

@Singleton
@LocalBean
@Startup
public class LocalPlatformPtoperties
{
	private String name;
	private URL address;
	private String ejbJNDIPrefix;
	
	@PostConstruct
	public void setup()
	{
		Properties properties;
		InputStream inputStream  = getClass().getClassLoader().getResourceAsStream("/resources/platform.properties");
		properties = new Properties();
		try
		{
			properties.load(inputStream);
			
			name = properties.getProperty("platform_name");
			address = new URL(properties.getProperty("platform_address"));
			ejbJNDIPrefix = properties.getProperty("agent_jndi_prefix");
		}
		catch (IOException e1)
		{
			// TODO properties.load catch block
			e1.printStackTrace();
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
