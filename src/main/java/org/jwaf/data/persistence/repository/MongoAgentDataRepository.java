package org.jwaf.data.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.bson.Document;
import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.common.data.mongo.UpdateFunction;
import org.jwaf.common.data.mongo.annotations.MongoDB;
import org.jwaf.common.data.mongo.annotations.MorphiaAdvancedDatastore;
import org.jwaf.common.data.mongo.annotations.MorphiaODM;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Stateless
@LocalBean
public class MongoAgentDataRepository
{
	public static final String AGENT_DB_NAME = "agent_data";
	
	@Inject @MorphiaAdvancedDatastore(AGENT_DB_NAME)
	private AdvancedDatastore ds;
	
	@Inject @MongoDB(AGENT_DB_NAME)
	private MongoDatabase db;
	
	@Inject @MorphiaODM
	private Morphia morphia;
	
	public MongoCollection<Document> getCollection(String colName)
	{
		return db.getCollection(colName);
	}
	
	public <T> Key<T> insert(String colName, T entity)
	{
		return ds.insert(colName, entity);
	}
	
	public <T> Iterable<Key<T>> insert(String colName, Iterable<T> entities)
	{
		return ds.insert(colName, entities);
	}
	
	public <T> Iterable<Key<T>> insert(String colName, Iterable<T> entities, WriteConcern wc)
	{
		return ds.insert(colName, entities, wc);
	}
	
	public <T> T find(String colName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		return query.get();
	}
	
	public <T> List<T> findAll(String colName, Class<T> type)
	{
		return ds.find(colName, type).asList();
	}
	
	public <T> List<T> findMany(String colName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		return query.asList();
	}
	
	public <T> WriteResult delete(String colName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		return ds.delete(query);
	}
	
	public <T> WriteResult delete(String colName, Class<T> type, QueryFunction<T> queryFunc, WriteConcern wc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		return ds.delete(query, wc);
	}
	
	public <T> long getCount(String colName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		return ds.getCount(query);
	}
	
	public <T> Key<T> save(String colName, T entity)
	{
		return ds.save(colName, entity);
	}
	
	public <T> Key<T> save(String colName, T entity, WriteConcern wc)
	{
		return ds.save(colName, entity, wc);
	}
	
	public <T> UpdateResults update(String colName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.update(query, updates);
	}
	
	public <T> UpdateResults update(String colName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, boolean createIfMissing)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.update(query, updates, createIfMissing);
	}
	
	public <T> UpdateResults update(String colName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, 
			boolean createIfMissing, WriteConcern wc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.update(query, updates, createIfMissing, wc);
	}
	
	public <T> T findAndDelete(String colName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		return ds.findAndDelete(query);
	}
	
	public <T> T findAndModify(String colName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.findAndModify(query, updates);
	}
	
	public <T> T findAndModify(String colName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, boolean oldVersion)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.findAndModify(query, updates, oldVersion);
	}
	
	public <T> T findAndModify(String colName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, 
			boolean oldVersion, boolean createIfMissing)
	{
		Query<T> query = createQuery(colName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.findAndModify(query, updates, oldVersion, createIfMissing);
	}

	private <T> Query<T> createQuery(String colName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = ds.createQuery(colName, type);
		query = queryFunc.apply(query);
		return query;
	}
	
	private <T> UpdateOperations<T> createUpdates(Class<T> type, UpdateFunction<T> updateFunc)
	{
		UpdateOperations<T> updates = ds.createUpdateOperations(type);
		updates = updateFunc.apply(updates);
		return updates;
	}
}
