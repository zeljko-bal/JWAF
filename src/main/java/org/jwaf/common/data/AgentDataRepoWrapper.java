package org.jwaf.common.data;

import java.util.Map;

public class AgentDataRepoWrapper implements DataRepository
{
	private AgentDataRepository dataRepo;
	private String agentName;
	
	public AgentDataRepoWrapper(AgentDataRepository dataRepo, String agentName)
	{
		this.dataRepo = dataRepo;
		this.agentName = agentName;
	}
	
	@Override
	public Map<String, String> getData()
	{
		return dataRepo.getData(agentName);
	}

	@Override
	public String put(String key, String value)
	{
		return dataRepo.put(agentName, key, value);
	}

	@Override
	public String remove(Object key)
	{
		return dataRepo.remove(agentName, key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m)
	{
		dataRepo.putAll(agentName, m);
	}

	@Override
	public void clear()
	{
		dataRepo.clear(agentName);
	}
}
