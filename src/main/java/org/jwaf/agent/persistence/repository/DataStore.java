package org.jwaf.agent.persistence.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.util.SerializationUtils;

public class DataStore implements Map<String, String>
{
	private AgentRepository agentRepo;
	
	private AgentDataType type;
	
	private String agentName;
	
	public DataStore(AgentRepository agentRepo, AgentDataType type, String agentName)
	{
		this.agentRepo = agentRepo;
		this.type = type;
		this.agentName = agentName;
	}
	
	private Map<String,String> getData()
	{
		return getAgent().getData(type);
	}
	
	private AgentEntity getAgent()
	{
		return agentRepo.findAgent(agentName);
	}
	
	private void merge(AgentEntity agent)
	{
		agentRepo.merge(agent);
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

	@Override
	public String put(String key, String value)
	{
		AgentEntity agent =  getAgent();
		String ret = agent.getData(type).put(key, value);
		merge(agent);
		return ret;
	}
	
	public Serializable putObject(String key, Serializable value)
	{
		return SerializationUtils.deSerialize(put(key, SerializationUtils.serialize(value)));
	}

	@Override
	public String remove(Object key)
	{
		AgentEntity agent =  getAgent();
		String ret = agent.getData(type).remove(key);
		merge(agent);
		return ret;
	}
	
	public Serializable removeObject(Object key)
	{
		return SerializationUtils.deSerialize(remove(key));
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m)
	{
		AgentEntity agent =  getAgent();
		agent.getData(type).putAll(m);
		merge(agent);
	}

	@Override
	public void clear()
	{
		AgentEntity agent =  getAgent();
		agent.getData(type).clear();
		merge(agent);
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
}
