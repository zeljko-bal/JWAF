package org.jwaf.data.management;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.bson.Document;
import org.jwaf.common.data.AgentDataRepoWrapper;
import org.jwaf.common.data.DataMap;
import org.jwaf.common.mongo.QueryFunction;
import org.jwaf.common.mongo.UpdateFunction;
import org.jwaf.data.persistence.repository.MongoAgentDataRepository;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateResults;

import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;

@Stateless
@LocalBean
public class AgentDataManager
{
	public static final String PUBLIC_DATA = "public_agent_data";
	
	@Inject
	private MongoAgentDataRepository dataRepo;
	
	public MongoCollection<Document> getCollection(String name)
	{
		return dataRepo.getCollection(name);
	}
	
	public <T> Key<T> insert(String name, T entity)
	{
		return dataRepo.insert(name, entity);
	}
	
	public <T> Iterable<Key<T>> insert(String name, Iterable<T> entities)
	{
		return dataRepo.insert(name, entities);
	}
	
	public <T> Iterable<Key<T>> insert(String name, Iterable<T> entities, WriteConcern wc)
	{
		return dataRepo.insert(name, entities, wc);
	}
	
	public <T> T find(String name, Class<T> type, QueryFunction<T> queryFunc)
	{
		return dataRepo.find(name, type, queryFunc);
	}
	
	public <T> List<T> findAll(String name, Class<T> type)
	{
		return dataRepo.findAll(name, type);
	}
	
	public <T> List<T> findMany(String name, Class<T> type, QueryFunction<T> queryFunc)
	{
		return dataRepo.findMany(name, type, queryFunc);
	}
	
	public <T> WriteResult delete(String name, Class<T> type, QueryFunction<T> queryFunc)
	{
		return dataRepo.delete(name, type, queryFunc);
	}
	
	public <T> WriteResult delete(String name, Class<T> type, QueryFunction<T> queryFunc, WriteConcern wc)
	{
		return dataRepo.delete(name, type, queryFunc, wc);
	}
	
	public <T> long getCount(String name, Class<T> type, QueryFunction<T> queryFunc)
	{
		return dataRepo.getCount(name, type, queryFunc);
	}
	
	public <T> Key<T> save(String name, T entity)
	{
		return dataRepo.save(name, entity);
	}
	
	public <T> Key<T> save(String name, T entity, WriteConcern wc)
	{
		return dataRepo.save(name, entity, wc);
	}
	
	public <T> UpdateResults update(String name, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc)
	{
		return dataRepo.update(name, type, queryFunc, updateFunc);
	}
	
	public <T> UpdateResults update(String name, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, boolean createIfMissing)
	{
		return dataRepo.update(name, type, queryFunc, updateFunc, createIfMissing);
	}
	
	public <T> UpdateResults update(String name, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, 
			boolean createIfMissing, WriteConcern wc)
	{
		return dataRepo.update(name, type, queryFunc, updateFunc, createIfMissing, wc);
	}
	
	public <T> T findAndDelete(String name, Class<T> type, QueryFunction<T> queryFunc)
	{
		return dataRepo.findAndDelete(name, type, queryFunc);
	}
	
	public <T> T findAndModify(String name, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc)
	{
		return dataRepo.findAndModify(name, type, queryFunc, updateFunc);
	}
	
	public <T> T findAndModify(String name, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, boolean oldVersion)
	{
		return dataRepo.findAndModify(name, type, queryFunc, updateFunc, oldVersion);
	}
	
	public <T> T findAndModify(String name, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, 
			boolean oldVersion, boolean createIfMissing)
	{
		return dataRepo.findAndModify(name, type, queryFunc, updateFunc, oldVersion, createIfMissing);
	}
	
	public Document getPublicData(String name)
	{
		return getCollection(name).find(Filters.eq("_id", PUBLIC_DATA)).first();
	}
	
	public void putPublicData(String name, Document data)
	{
		getCollection(name).findOneAndReplace(Filters.eq("_id", PUBLIC_DATA), data, 
				new FindOneAndReplaceOptions().upsert(true));
	}
	
	public DataMap getPublicDataMap(String name)
	{
		return createDataMap(name, PUBLIC_DATA);
	}
	
	public DataMap createDataMap(String name, String dataName)
	{
		return new DataMap(
				new AgentDataRepoWrapper(
						new MongoDataRepoWrapper(dataName, this), name));
	}
	
	public String getAllDataAsString(String name)
	{
		List<Document> allData = new ArrayList<>();
		
		getCollection(name).find().forEach((Document d)->allData.add(d));
		
		return new Document(name, allData).toJson();
	}

	public void initializeData(String name, String data)
	{
		@SuppressWarnings("unchecked")
		List<Document> allData = (List<Document>) Document.parse(data).get(name);
		getCollection(name).insertMany(allData);
	}
}
