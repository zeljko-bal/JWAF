package org.jwaf.platform.management;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS REST api configuration class.
 * 
 * @author zeljko.bal
 */
@ApplicationPath("/")
public class RESTApplication extends Application
{
	public RESTApplication()
	{}
}