package org.jwaf.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.bson.Document;

/**
 * A helper class that helps with reading the property files from the file system.
 * 
 * @author zeljko.bal
 */
public class PropertiesUtils
{
	/**
	 * Reads a ".properties" file into a {@link Properties} object.
	 * 
	 * @param name of the file
	 * @return read properties
	 * @throws IOException
	 */
	public static Properties getProperties(String name) throws IOException
	{
		Properties properties = new Properties();
		
		properties.load(getInputStream(name));
		
		return properties;
	}
	
	/**
	 * Reads a jason file into a {@link Document} instance.
	 * 
	 * @param name of the file
	 * @return read properties
	 * @throws IOException
	 */
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
