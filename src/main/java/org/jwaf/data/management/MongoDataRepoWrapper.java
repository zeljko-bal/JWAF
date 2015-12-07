package org.jwaf.data.management;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jwaf.common.data.map.AgentDataMapRepository;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

public class MongoDataRepoWrapper implements AgentDataMapRepository
{
	private AgentDataManager dataManager;
	private Bson query;
	
	public MongoDataRepoWrapper(String mapName, AgentDataManager dataManager)
	{
		this.dataManager = dataManager;
		query = Filters.eq("_id", mapName);
	}
	
	@Override
	public Map<String, String> getData(String agentName)
	{
		Map<String, String> ret = new HashMap<>();
		
		dataManager.getCollection(agentName)
				.find(query)
				.first()
				.forEach((k,v)->ret.put(k, v.toString()));
		
		return ret;
	}

	@Override
	public String put(String agentName, String key, String value)
	{
		dataManager.getCollection(agentName).updateOne(query, 
				new Document("$set", new Document(key, value)), 
				new UpdateOptions().upsert(true));
		
		return value;
	}

	@Override
	public String remove(String agentName, Object key)
	{
		String ret = getData(agentName).get(key);
		
		dataManager.getCollection(agentName).updateOne(query, 
				new Document("$unset", new Document(key.toString(), "")), 
				new UpdateOptions().upsert(true));
		
		return ret;
	}

	@Override
	public void putAll(String agentName, Map<? extends String, ? extends String> m)
	{
		Map<String, Object> toInsert = new HashMap<String, Object>(m);
		
		dataManager.getCollection(agentName).updateOne(query, 
				new Document("$set", new Document(toInsert)), 
				new UpdateOptions().upsert(true));
	}

	@Override
	public void clear(String agentName)
	{
		dataManager.getCollection(agentName).deleteOne(query);
	}
}
