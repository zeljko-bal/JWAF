package org.jwaf.service.persistence.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.common.data.mongo.annotations.MorphiaDatastore;
import org.jwaf.service.persistence.entity.AgentServiceType;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

/**
 * A repository bean that contains methods for crud operations on {@link AgentServiceType}.
 * 
 * @author zeljko.bal
 */
@Stateless
@LocalBean
public class AgentServiceRepository
{
	@Inject @MorphiaDatastore
	private Datastore ds;
	
	public AgentServiceType find(String name)
	{
		return ds.get(AgentServiceType.class, name);
	}
	
	public List<String> find(QueryFunction<AgentServiceType> queryFunc)
	{
		Query<AgentServiceType> query = ds.createQuery(AgentServiceType.class);
		query = queryFunc.apply(query);
		return find(query);
	}
	
	public List<String> find(Map<String, String> attributes)
	{
		Query<AgentServiceType> query = ds.createQuery(AgentServiceType.class);
		
		for(String key : attributes.keySet())
		{
			query = query.field("attributes."+key).equal(attributes.get(key));
		}
		
		return find(query);
	}
	
	private List<String> find(Query<AgentServiceType> query)
	{
		return query.retrievedFields(true, "name")
			  	.asList()
			  	.stream()
			  	.map(t -> t.getName())
			  	.collect(Collectors.toList());
	}
	
	public void register(AgentServiceType service)
	{
		ds.save(service);
	}
}
