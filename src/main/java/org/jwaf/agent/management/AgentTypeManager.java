package org.jwaf.agent.management;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentTypeRepository;
import org.jwaf.common.data.mongo.QueryFunction;

@Stateless
@LocalBean
public class AgentTypeManager
{
	@Inject
	private AgentTypeRepository typeRepo;
	
	public AgentType find(String name)
	{
		return typeRepo.find(name);
	}
	
	public List<AgentType> find(Map<String, String> attributes)
	{
		return typeRepo.find(attributes);
	}
	
	public List<AgentType> find(QueryFunction<AgentType> queryFunc)
	{
		return typeRepo.find(queryFunc);
	}
	
	public void create(AgentType type)
	{
		typeRepo.create(type);
	}
	
	public void remove(String name)
	{
		typeRepo.remove(name);
	}
}
