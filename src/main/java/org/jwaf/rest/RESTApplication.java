package org.jwaf.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class RESTApplication extends ResourceConfig
{
	public RESTApplication()
	{
		packages("org.jwaf");
	}
}