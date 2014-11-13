package org.jwaf.agent.management;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void create(AgentType type)
	{
		typeRepo.create(type);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void remove(String name)
	{
		typeRepo.remove(name);
	}
}
