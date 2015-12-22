package org.jwaf.base.tools;

import java.util.HashMap;
import java.util.Map;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Serialized;

/**
 * An entity class for storing serialized agent data.
 * @see AutoPersister
 * 
 * @author zeljko.bal
 */
@Entity
public class PersistentFields
{
	public static final String ID = "AUTO_PERSISTENT_FIELDS";
	
	@Id
	private String id;
	
	@Serialized
	private Map<String, Object> fields;
	
	public PersistentFields()
	{
		fields = new HashMap<>();
		id = ID;
	}
	
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public Map<String, Object> getFields()
	{
		return fields;
	}
}