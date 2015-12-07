package org.jwaf.base.tools;

import java.util.List;
import java.util.Map;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.AgentTypeManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.common.data.mongo.QueryFunction;

public class TypeTools
{
	private AgentManager agentManager;
	private AgentTypeManager typeManager;

	public TypeTools(AgentManager agentManager, AgentTypeManager typeManager)
	{
		this.agentManager = agentManager;
		this.typeManager = typeManager;
	}

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
	
	public List<AgentType> find(QueryFunction<AgentType> queryFunc)
	{
		return typeManager.find(queryFunc);
	}
}
