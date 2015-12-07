package org.jwaf.base.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AutoPersister
{
	private Object owner;
	private List<Field> ownerFields;
	private DataTools dataTools;
	
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
		ownerFields.forEach(field -> 
		{
			try
			{
				dataTools.save(field.get(owner));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public void autoLoad()
	{
		ownerFields.forEach(field -> 
		{
			try
			{
				Object value = dataTools.find(field.getType(), 
						q->q.field("_id").equal(field.getName()));
				
				field.set(owner, value);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
