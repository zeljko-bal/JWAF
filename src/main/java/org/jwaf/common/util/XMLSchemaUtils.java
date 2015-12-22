package org.jwaf.common.util;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * A helper class for generating XML schemas based on JAXB entities.
 * 
 * @author zeljko.bal
 */
public class XMLSchemaUtils
{
	public static String generate(Class<?> c) throws IOException, JAXBException
	{
		StringWriter writer = new StringWriter();  
		
		JAXBContext jaxbContext = JAXBContext.newInstance(c);
		
		jaxbContext.generateSchema(new SchemaOutputResolver()
		{
		    @Override  
		    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException 
		    {
		        final StreamResult result = new StreamResult(writer);  
		        result.setSystemId("no-id"); // Result MUST contain system id, or JAXB throws an error message  
		        return result;
		    }
		});
		
		return writer.toString();
	}
}
