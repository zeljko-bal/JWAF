package org.jwaf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.sun.jersey.core.util.Base64;

public class SerializationUtils
{
	public static Serializable deSerialize(String string)
	{
		if(string == null)
		{
			return null;
		}
		
		byte[] bytes = Base64.decode(string.getBytes());
		Serializable object = null;
		
		try 
		{
			ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			object = (Serializable) objectInputStream.readObject();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		catch (ClassCastException e) {
			e.printStackTrace();
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
			encoded = new String(Base64.encode(byteArrayOutputStream.toByteArray()));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return encoded;
	}
}
