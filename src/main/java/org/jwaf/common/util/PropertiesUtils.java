package org.jwaf.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.bson.Document;

public class PropertiesUtils
{
	public static Properties getProperties(String name) throws IOException
	{
		Properties properties = new Properties();
		
		properties.load(getInputStream(name));
		
		return properties;
	}
	
	public static Document getJsonProperties(String name)
	{
		return Document.parse(getInputStream(name).toString());
	}
	
	private static InputStream getInputStream(String name)
	{
		return PropertiesUtils.class.getClassLoader().getResourceAsStream(name);
	}
}
