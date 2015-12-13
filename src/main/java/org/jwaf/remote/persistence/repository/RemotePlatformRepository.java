package org.jwaf.remote.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.data.mongo.annotations.MorphiaAdvancedDatastore;
import org.jwaf.remote.persistence.entity.AgentPlatform;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

@Stateless
@LocalBean
public class RemotePlatformRepository
{
	@Inject @MorphiaAdvancedDatastore
	private AdvancedDatastore ds;
	
	public AgentPlatform findPlatform(String name)
	{
		return ds.get(AgentPlatform.class, name);
	}
	
	public boolean containsPlatform(String name)
	{
		return ds.find(AgentPlatform.class, "name", name)
				 .countAll() > 0;
	}
	
	public boolean containsAid(String name)
	{
		return ds.find(AgentPlatform.class)
				 .field("agentIds").hasThisOne(name)
				 .countAll() > 0;
	}
	
	public boolean containsAid(String agentName, String platformName)
	{
		return ds.find(AgentPlatform.class, "name", platformName)
				 .field("agentIds").hasThisOne(agentName)
				 .countAll() > 0;
	}
	
	public void registerPlatform(AgentPlatform platform)
	{
		ds.insert(platform);
	}
	
	public void register(String agentName, String platformName)
	{
		Query<AgentPlatform> query = ds.find(AgentPlatform.class, "name", platformName);
		UpdateOperations<AgentPlatform> updates = ds.createUpdateOperations(AgentPlatform.class)
				.add("agentIds", agentName);
		ds.update(query, updates);
	}
	
	public List<AgentIdentifier> getAgentIds(String platformName)
	{
		return findPlatform(platformName).getAgentIds();
	}
	
	public List<AgentPlatform> getAllPlatforms()
	{
		return ds.find(AgentPlatform.class).asList();
	}
	
	public void unregisterPlatform(String platformName)
	{
		ds.delete(AgentPlatform.class, platformName);
	}
	
	public void unregister(String agentName, String platformName)
	{
		Query<AgentPlatform> query = ds.find(AgentPlatform.class, "name", platformName);
		UpdateOperations<AgentPlatform> updates = ds.createUpdateOperations(AgentPlatform.class)
				.removeAll("agentIds", agentName);
		ds.update(query, updates);
	}
	
	public AgentPlatform locationOf(String agentName)
	{
		return ds.createQuery(AgentPlatform.class)
				 .field("agentIds")
				 .hasThisOne(agentName)
				 .get();
	}
}
