package org.jwaf.base.tools;

import java.util.List;

import org.bson.Document;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.common.data.map.DataMap;
import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.common.data.mongo.UpdateFunction;
import org.jwaf.data.management.AgentDataManager;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateResults;

import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;

/**
 * A facade exposing functionalities of platform manager beans that deal with agent's own data.
 * 
 * @author zeljko.bal
 */
public class DataTools
{
	private AgentDataManager agentDataManager;
	private AgentIdentifier aid;
	
	public DataTools(AgentDataManager agentDataManager)
	{
		this.agentDataManager = agentDataManager;
	}
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}
	
	public MongoCollection<Document> getCollection()
	{
		return agentDataManager.getAgentCollection(aid.getName());
	}
	
	public <T> Key<T> insert(T entity)
	{
		return agentDataManager.insert(aid.getName(), entity);
	}
	
	public <T> Iterable<Key<T>> insert(Iterable<T> entities)
	{
		return agentDataManager.insert(aid.getName(), entities);
	}
	
	public <T> Iterable<Key<T>> insert(Iterable<T> entities, WriteConcern wc)
	{
		return agentDataManager.insert(aid.getName(), entities, wc);
	}
	
	public <T> T find(Class<T> type, Object id)
	{
		return agentDataManager.find(aid.getName(), type, id);
	}
	
	public <T> T find(Class<T> type, QueryFunction<T> queryFunc)
	{
		return agentDataManager.find(aid.getName(), type, queryFunc);
	}
	
	public <T> List<T> findAll(Class<T> type)
	{
		return agentDataManager.findAll(aid.getName(), type);
	}
	
	public <T> List<T> findMany(Class<T> type, QueryFunction<T> queryFunc)
	{
		return agentDataManager.findMany(aid.getName(), type, queryFunc);
	}
	
	public <T> WriteResult delete(Class<T> type, QueryFunction<T> queryFunc)
	{
		return agentDataManager.delete(aid.getName(), type, queryFunc);
	}
	
	public <T> WriteResult delete(Class<T> type, QueryFunction<T> queryFunc, WriteConcern wc)
	{
		return agentDataManager.delete(aid.getName(), type, queryFunc, wc);
	}
	
	public <T> long getCount(Class<T> type, QueryFunction<T> queryFunc)
	{
		return agentDataManager.getCount(aid.getName(), type, queryFunc);
	}
	
	public <T> Key<T> save(T entity)
	{
		return agentDataManager.save(aid.getName(), entity);
	}
	
	public <T> Key<T> save(T entity, WriteConcern wc)
	{
		return agentDataManager.save(aid.getName(), entity, wc);
	}
	
	public <T> UpdateResults update(Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc)
	{
		return agentDataManager.update(aid.getName(), type, queryFunc, updateFunc);
	}
	
	public <T> UpdateResults update(Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, boolean createIfMissing)
	{
		return agentDataManager.update(aid.getName(), type, queryFunc, updateFunc, createIfMissing);
	}
	
	public <T> UpdateResults update(Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, 
			boolean createIfMissing, WriteConcern wc)
	{
		return agentDataManager.update(aid.getName(), type, queryFunc, updateFunc, createIfMissing, wc);
	}
	
	public <T> T findAndDelete(Class<T> type, QueryFunction<T> queryFunc)
	{
		return agentDataManager.findAndDelete(aid.getName(), type, queryFunc);
	}
	
	public <T> T findAndModify(Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc)
	{
		return agentDataManager.findAndModify(aid.getName(), type, queryFunc, updateFunc);
	}
	
	public <T> T findAndModify(Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, boolean oldVersion)
	{
		return agentDataManager.findAndModify(aid.getName(), type, queryFunc, updateFunc, oldVersion);
	}
	
	public <T> T findAndModify(Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, 
			boolean oldVersion, boolean createIfMissing)
	{
		return agentDataManager.findAndModify(aid.getName(), type, queryFunc, updateFunc, oldVersion, createIfMissing);
	}

	public String getAllDataAsString()
	{
		return agentDataManager.getAllDataAsString(aid.getName());
	}

	public void initializeData(String data)
	{
		agentDataManager.initializeData(aid.getName(), data);
	}
	
	public Document getPublicData()
	{
		return agentDataManager.getPublicData(aid.getName());
	}
	
	public void putPublicData(Document data)
	{
		agentDataManager.putPublicData(aid.getName(), data);
	}
	
	public DataMap getPublicDataMap()
	{
		return agentDataManager.createPublicDataMap(aid.getName());
	}
	
	public DataMap map(String dataName)
	{
		return agentDataManager.createAgentDataMap(aid.getName(), dataName);
	}
}
