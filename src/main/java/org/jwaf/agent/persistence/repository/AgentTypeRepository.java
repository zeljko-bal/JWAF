package org.jwaf.agent.persistence.repository;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.common.data.mongo.annotations.MorphiaDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

/**
 * A repository bean that contains methods for crud operations on {@link AgentType}.
 * 
 * @author zeljko.bal
 */
@Stateless
@LocalBean
public class AgentTypeRepository
{
	@Inject @MorphiaDatastore
	private Datastore ds;
	
	public AgentType find(String name)
	{
		return ds.get(AgentType.class, name);
	}
	
	public List<AgentType> find(QueryFunction<AgentType> queryFunc)
	{
		Query<AgentType> query = ds.createQuery(AgentType.class);
		query = queryFunc.apply(query);
		return find(query);
	}
	
	public List<AgentType> find(Map<String, String> attributes)
	{
		Query<AgentType> query = ds.createQuery(AgentType.class);
		
		for(String key : attributes.keySet())
		{
			query = query.field("attributes."+key).equal(attributes.get(key));
		}
		
		return find(query);
	}
	
	private List<AgentType> find(Query<AgentType> query)
	{
		return query.asList();
	}
	
	public void create(AgentType type)
	{
		ds.save(type);
	}
	
	public void remove(String name)
	{
		ds.delete(AgentType.class, name);
	}
}
