package org.jwaf.data.persistence.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jwaf.data.persistence.entity.AgentDataType;
import org.jwaf.util.SerializationUtils;

public class DataStore implements Map<String, String>
{
	private AgentDataRepository dataRepo;
	private AgentDataType dataType;
	private String agentName;
	
	public DataStore(AgentDataRepository dataRepo, AgentDataType dataType, String agentName)
	{
		this.dataRepo = dataRepo;
		this.dataType = dataType;
		this.agentName = agentName;
	}
	
	private Map<String,String> getData()
	{
		return dataRepo.getData(agentName, dataType);
	}

	@Override
	public int size()
	{
		return getData().size();
	}

	@Override
	public boolean isEmpty()
	{
		return getData().isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return getData().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return getData().containsValue(value);
	}

	@Override
	public String get(Object key)
	{
		return getData().get(key);
	}
	
	public Serializable getObject(Object key)
	{
		return SerializationUtils.deSerialize(get(key));
	}
	
	public Object getObjectOrDefault(String key, Object defaultValue)
	{
		Object value = getObject(key);
		
		if(value != null)
		{
			return value;
		}
		else
		{
			return defaultValue;
		}
	}
	
	public int getInt(String key)
	{
		return Integer.parseInt(get(key));
	}
	
	public int getIntOrDefault(String key, int defaultValue)
	{
		try
		{
			return Integer.parseInt(get(key));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	@Override
	public String put(String key, String value)
	{
		return dataRepo.put(agentName, dataType, key, value);
	}
	
	public Serializable putObject(String key, Serializable value)
	{
		return SerializationUtils.deSerialize(put(key, SerializationUtils.serialize(value)));
	}

	@Override
	public String remove(Object key)
	{
		return dataRepo.remove(agentName, dataType, key);
	}
	
	public Serializable removeObject(Object key)
	{
		return SerializationUtils.deSerialize(remove(key));
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m)
	{
		dataRepo.putAll(agentName, dataType, m);
	}

	@Override
	public void clear()
	{
		dataRepo.clear(agentName, dataType);
	}

	@Override
	public Set<String> keySet()
	{
		return getData().keySet();
	}

	@Override
	public Collection<String> values()
	{
		return getData().values();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet()
	{
		return getData().entrySet();
	}
	
	public String append(String key, String content)
	{
		return put(key, getOrDefault(key, "")+content);
	}
	
	public String prepend(String key, String content)
	{
		return put(key, content+getOrDefault(key, ""));
	}
	
	public int increment(String key)
	{
		int value = getIntOrDefault(key, 0);
		Integer newValue = value + 1;
		put(key, newValue.toString());
		return value;
	}
	
	public int decrement(String key)
	{
		int value = getIntOrDefault(key, 0);
		Integer newValue = value - 1;
		put(key, newValue.toString());
		return value;
	}
}
