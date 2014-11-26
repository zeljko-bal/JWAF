package org.jwaf.agent.management;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentTypeRepository;

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
	
	public void create(AgentType type)
	{
		typeRepo.create(type);
	}
	
	public void remove(String name)
	{
		typeRepo.remove(name);
	}
}
