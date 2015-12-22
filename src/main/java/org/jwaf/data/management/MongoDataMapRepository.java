package org.jwaf.data.management;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jwaf.common.data.map.DataMapRepository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

/**
 * An implementation of the {@link DataMapRepository} using MongoDB.
 * 
 * @author zeljko.bal
 */
public class MongoDataMapRepository implements DataMapRepository
{
	private MongoCollection<Document> collection;
	private String documentId;
	
	public MongoDataMapRepository(MongoCollection<Document> collection, String documentId)
	{
		this.collection = collection;
		this.documentId = documentId;
	}
	
	@Override
	public Map<String, String> getData()
	{
		Map<String, String> ret = new HashMap<>();
		
		collection.find(query())
				.first()
				.forEach((k,v)->ret.put(k, convertToString(v)));
		
		return ret;
	}

	@Override
	public String put(String key, String value)
	{
		collection.updateOne(query(), 
				new Document("$set", new Document(key, value)), 
				new UpdateOptions().upsert(true));
		
		return value;
	}

	@Override
	public String remove(Object key)
	{
		String ret = getData().get(key);
		
		collection.updateOne(query(), 
				new Document("$unset", new Document(key.toString(), "")), 
				new UpdateOptions().upsert(true));
		
		return ret;
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m)
	{
		Map<String, Object> toInsert = new HashMap<String, Object>(m);
		
		collection.updateOne(query(), 
				new Document("$set", new Document(toInsert)), 
				new UpdateOptions().upsert(true));
	}

	@Override
	public void clear()
	{
		collection.deleteOne(query());
	}
	
	private String convertToString(Object o)
	{
		if(o == null) return null;
		else return o.toString();
	}

	@Override
	public boolean exists()
	{
		return collection.find(query()).first() != null;
	}
	
	private Bson query()
	{
		return Filters.eq("_id", documentId);
	}
}
