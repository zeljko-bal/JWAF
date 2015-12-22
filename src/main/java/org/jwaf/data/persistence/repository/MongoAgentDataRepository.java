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

/**
 * A repository bean that contains methods for CRUD operations on agent data.
 * 
 * @author zeljko.bal
 */
@Stateless
@LocalBean
public class MongoAgentDataRepository
{
	public static final String COLLECTION_PREFIX = "agent_data.";
	
	@Inject @MorphiaAdvancedDatastore
	private AdvancedDatastore ds;
	
	@Inject @MongoDB
	private MongoDatabase db;
	
	@Inject @MorphiaODM
	private Morphia morphia;
	
	public MongoCollection<Document> getAgentCollection(String agentName)
	{
		return db.getCollection(createCollectionName(agentName));
	}
	
	public MongoCollection<Document> getCollection(String collectionName)
	{
		return db.getCollection(collectionName);
	}
	
	public <T> Key<T> insert(String agentName, T entity)
	{
		return ds.insert(createCollectionName(agentName), entity);
	}
	
	public <T> Iterable<Key<T>> insert(String agentName, Iterable<T> entities)
	{
		return ds.insert(createCollectionName(agentName), entities);
	}
	
	public <T> Iterable<Key<T>> insert(String agentName, Iterable<T> entities, WriteConcern wc)
	{
		return ds.insert(createCollectionName(agentName), entities, wc);
	}
	
	public <T> T find(String agentName, Class<T> type, Object id)
	{
		return find(agentName, type, q->q.field("_id").equal(id));
	}
	
	public <T> T find(String agentName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		return query.get();
	}
	
	public <T> List<T> findAll(String agentName, Class<T> type)
	{
		return ds.find(createCollectionName(agentName), type).asList();
	}
	
	public <T> List<T> findMany(String agentName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		return query.asList();
	}
	
	public <T> WriteResult delete(String agentName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		return ds.delete(query);
	}
	
	public <T> WriteResult delete(String agentName, Class<T> type, QueryFunction<T> queryFunc, WriteConcern wc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		return ds.delete(query, wc);
	}
	
	public <T> long getCount(String agentName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		return ds.getCount(query);
	}
	
	public <T> Key<T> save(String agentName, T entity)
	{
		return ds.save(createCollectionName(agentName), entity);
	}
	
	public <T> Key<T> save(String agentName, T entity, WriteConcern wc)
	{
		return ds.save(createCollectionName(agentName), entity, wc);
	}
	
	public <T> UpdateResults update(String agentName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.update(query, updates);
	}
	
	public <T> UpdateResults update(String agentName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, boolean createIfMissing)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.update(query, updates, createIfMissing);
	}
	
	public <T> UpdateResults update(String agentName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, 
			boolean createIfMissing, WriteConcern wc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.update(query, updates, createIfMissing, wc);
	}
	
	public <T> T findAndDelete(String agentName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		return ds.findAndDelete(query);
	}
	
	public <T> T findAndModify(String agentName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.findAndModify(query, updates);
	}
	
	public <T> T findAndModify(String agentName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, boolean oldVersion)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.findAndModify(query, updates, oldVersion);
	}
	
	public <T> T findAndModify(String agentName, Class<T> type, 
			QueryFunction<T> queryFunc, UpdateFunction<T> updateFunc, 
			boolean oldVersion, boolean createIfMissing)
	{
		Query<T> query = createQuery(agentName, type, queryFunc);
		UpdateOperations<T> updates = createUpdates(type, updateFunc);
		
		return ds.findAndModify(query, updates, oldVersion, createIfMissing);
	}

	private <T> Query<T> createQuery(String agentName, Class<T> type, QueryFunction<T> queryFunc)
	{
		Query<T> query = ds.createQuery(createCollectionName(agentName), type);
		query = queryFunc.apply(query);
		if(!createCollectionName(agentName).equals(query.getCollection().getName()))
		{
			throw new IllegalArgumentException("MongoAgentDataRepository: supplied QueryFunction returned a query for a different collection.");
		}
		return query;
	}
	
	private <T> UpdateOperations<T> createUpdates(Class<T> type, UpdateFunction<T> updateFunc)
	{
		UpdateOperations<T> updates = ds.createUpdateOperations(type);
		updates = updateFunc.apply(updates);
		return updates;
	}
	
	private String createCollectionName(String agentName)
	{
		return COLLECTION_PREFIX+agentName;
	}
}
