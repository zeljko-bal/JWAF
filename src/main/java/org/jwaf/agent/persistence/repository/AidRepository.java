package org.jwaf.agent.persistence.repository;

import java.net.URL;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.data.mongo.annotations.MorphiaAdvancedDatastore;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

@Stateless
@LocalBean
public class AidRepository
{
	@Inject @MorphiaAdvancedDatastore
	private AdvancedDatastore ds;
	
	public AgentIdentifier insert(AgentIdentifier aid)
	{
		ds.insert(aid);
		return aid;
	}
	
	public AgentIdentifier save(AgentIdentifier aid)
	{
		ds.save(aid);
		return aid;
	}
	
	public AgentIdentifier find(String name)
	{
		return ds.get(AgentIdentifier.class, name);
	}
	
	public List<AgentIdentifier> getAll()
	{
		return ds.find(AgentIdentifier.class).asList();
	}
	
	public void changeMTAddresses(String aidName, List<URL> mtAddresses)
	{
		Query<AgentIdentifier> query = ds.find(AgentIdentifier.class, "name", aidName);
		UpdateOperations<AgentIdentifier> updates = ds.createUpdateOperations(AgentIdentifier.class)
				.set("addresses", mtAddresses);
		
		ds.update(query, updates, true);
	}

	public void remove(String name)
	{
		ds.delete(AgentIdentifier.class, name);
	}
}
