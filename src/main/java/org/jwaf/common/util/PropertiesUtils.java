package org.jwaf.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.bson.Document;

public class PropertiesUtils
{
	public static Properties getProperties(String name) throws IOException
	{
		Properties properties = new Properties();
		
		properties.load(getInputStream(name));
		
		return properties;
	}
	
	public static Document getJsonProperties(String name) throws IOException
	{
		StringWriter writer = new StringWriter();
		IOUtils.copy(getInputStream(name), writer);
		return Document.parse(writer.toString());
	}
	
	private static InputStream getInputStream(String name)
	{
		return PropertiesUtils.class.getClassLoader().getResourceAsStream(name);
	}
}
