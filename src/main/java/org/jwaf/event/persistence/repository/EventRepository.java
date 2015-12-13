package org.jwaf.event.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.data.mongo.annotations.MorphiaDatastore;
import org.jwaf.event.persistence.entity.EventEntity;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

@Stateless
@LocalBean
public class EventRepository
{
	@Inject @MorphiaDatastore
	private Datastore ds;
	
	public EventEntity find(String name)
	{
		return ds.get(EventEntity.class, name);
	}
	
	public List<EventEntity> getAll()
	{
		return ds.find(EventEntity.class).asList();
	}
	
	public boolean exists(String name)
	{
		return ds.find(EventEntity.class, "name", name).countAll() > 0;
	}
	
	public void register(String name, String type)
	{
		ds.save(new EventEntity(name, type));
	}
	
	public void unregister(String eventName)
	{
		ds.delete(EventEntity.class, eventName);
	}
	
	public void subscribe(String agentName, String eventName)
	{
		Query<EventEntity> query = ds.find(EventEntity.class, "name", eventName);
		UpdateOperations<EventEntity> updates = ds.createUpdateOperations(EventEntity.class)
				.add("registeredAgents", new AgentIdentifier(agentName));
		ds.update(query, updates);
	}
	
	public void unsubscribe(String agentName)
	{
		Query<EventEntity> query = ds.find(EventEntity.class);
		unsubscribe(agentName, query);
	}
	
	public void unsubscribe(String agentName, String eventName)
	{
		Query<EventEntity> query = ds.find(EventEntity.class, "name", eventName);
		unsubscribe(agentName, query);
	}
	
	public void unsubscribe(String agentName, Query<EventEntity> query)
	{
		UpdateOperations<EventEntity> updates = ds.createUpdateOperations(EventEntity.class)
				.removeAll("registeredAgents", new AgentIdentifier(agentName));
		ds.update(query, updates);
	}
}
