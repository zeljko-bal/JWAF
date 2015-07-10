package org.jwaf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationUtils
{
	private static Logger log = LoggerFactory.getLogger("SerializationUtils");
	
	public static Serializable deSerialize(String string)
	{
		if(string == null)
		{
			return null;
		}
		
		byte[] bytes = Base64.getDecoder().decode(string.getBytes());
		Serializable object = null;
		
		try 
		{
			ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			object = (Serializable) objectInputStream.readObject();
		} 
		catch (IOException e) {
			log.error("deserialization error", e);
		} 
		catch (ClassNotFoundException e) {
			log.error("deserialization error", e);
		} 
		catch (ClassCastException e) {
			log.error("deserialization error", e);
		}
		
		return object;
	}

	public static String serialize(Serializable object)
	{
		if(object == null)
		{
			return null;
		}
		
		String encoded = null;

		try
		{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			encoded = new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()));
		} 
		catch (IOException e)
		{
			log.error("serialization error", e);
		}

		return encoded;
	}
}
