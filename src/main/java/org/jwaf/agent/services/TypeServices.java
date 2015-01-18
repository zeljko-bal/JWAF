package org.jwaf.agent.services;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;

@Stateless
@LocalBean
public class TypeServices
{
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private AgentTypeManager typeManager;
	
	public AgentType getTypeOf(AgentIdentifier aid)
	{
		return agentManager.findView(aid.getName()).getType();
	}
	
	public AgentType getTypeOf(String name)
	{
		return agentManager.findView(name).getType();
	}
	
	public AgentType find(String typeName)
	{
		return typeManager.find(typeName);
	}
	
	public List<AgentType> find(Map<String, String> attributes)
	{
		return typeManager.find(attributes);
	}
}
