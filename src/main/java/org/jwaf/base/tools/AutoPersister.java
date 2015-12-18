package org.jwaf.base.tools;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AutoPersister
{
	private Object owner;
	private List<Field> ownerFields;
	private DataTools dataTools;
	private AgentLogger log;
	
	public AutoPersister(Object owner, DataTools dataTools, boolean doScanFields)
	{
		this.owner = owner;
		this.dataTools = dataTools;
		ownerFields = new ArrayList<>();
		if(doScanFields) scanFields();
	}
	
	public void scanFields()
	{
		for(Field field : owner.getClass().getDeclaredFields())
		{
			if(field.isAnnotationPresent(Persistent.class))
			{
				field.setAccessible(true);
				ownerFields.add(field);
			}
		}
	}
	
	public void autoPersist()
	{
		PersistentFields data = new PersistentFields();
		
		for(Field field : ownerFields)
		{
			try
			{
				data.getFields().put(field.getName(), field.get(owner));
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				log.error("Unable to auto-persist field <{}>", field.getName(), e);
			}
		}
		
		dataTools.save(data);
	}
	
	public void autoLoad()
	{
		PersistentFields data = dataTools.find(PersistentFields.class, PersistentFields.ID);
		
		for(Field field : ownerFields)
		{
			Object value = null;
			
			try
			{
				if(data != null)
				{
					value = data.getFields().get(field.getName());
				}
				
				if(value == null)
				{
					if(field.getType().isPrimitive())
					{
						continue;
					}
					else
					{
						Class<?> type = field.getAnnotation(Persistent.class).type();
						
						// if not default value
						if(!Serializable.class.equals(type))
						{
							value = type.getDeclaredConstructor().newInstance();
						}
					}
				}
				
				field.set(owner, value);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				log.error("Unable to auto-load field <{}>", field.getName(), e);
			}
			catch (InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e)
			{
				log.error("Unable to auto-instatiate field <{}>", field.getName(), e);
			}
		}
	}
}
