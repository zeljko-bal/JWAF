package org.jwaf.data.persistence.repository;

import java.util.Map;

import org.jwaf.common.data.AgentDataRepository;
import org.jwaf.data.persistence.entity.AgentDataType;

public class PersistentDataRepoWrapper implements AgentDataRepository
{
	private PersistentAgentDataRepository dataRepo;
	private AgentDataType type;
	
	public PersistentDataRepoWrapper(PersistentAgentDataRepository dataRepo, AgentDataType type)
	{
		this.dataRepo = dataRepo;
		this.type = type;
	}

	@Override
	public Map<String, String> getData(String agentName)
	{
		return dataRepo.getData(agentName, type);
	}

	@Override
	public String put(String agentName, String key, String value)
	{
		return dataRepo.put(agentName, type, key, value);
	}

	@Override
	public String remove(String agentName, Object key)
	{
		return dataRepo.remove(agentName, type, key);
	}

	@Override
	public void putAll(String agentName, Map<? extends String, ? extends String> m)
	{
		dataRepo.putAll(agentName, type, m);
	}

	@Override
	public void clear(String agentName)
	{
		dataRepo.clear(agentName, type);
	}
}
