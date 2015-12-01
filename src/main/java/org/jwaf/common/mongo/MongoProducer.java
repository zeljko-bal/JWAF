package org.jwaf.common.mongo;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.lang3.StringUtils;
import org.jwaf.common.mongo.annotations.MongoDB;
import org.jwaf.common.mongo.annotations.MorphiaAdvancedDatastore;
import org.jwaf.common.mongo.annotations.MorphiaDatastore;
import org.jwaf.common.mongo.annotations.MorphiaODM;
import org.jwaf.common.mongo.annotations.SystemMongoClient;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@Singleton
@LocalBean
@Startup
public class MongoProducer
{
	private MongoClient mongoClient;
	private Morphia morphia;
	
	@PostConstruct
	public void setup()
	{
		// TODO add mongo.properties for connection strings and username/pass
		mongoClient = new MongoClient();
		
		morphia = new Morphia();
	}
	
	@Produces @SystemMongoClient
	public MongoClient getMongoClient()
	{
		return mongoClient;
	}
	
	@Produces @MongoDB
	public MongoDatabase getDatabase(InjectionPoint ip)
	{
		String dbName = ip.getAnnotated().getAnnotation(MongoDB.class).value();
		if(StringUtils.isNotEmpty(dbName))
		{
			return mongoClient.getDatabase(dbName);
		}
		else
		{
			return null;
		}
	}
	
	@Produces @MorphiaODM
	public Morphia getODM()
	{
		return morphia;
	}
	
	@Produces @MorphiaDatastore
	public Datastore createDatastore(InjectionPoint ip)
	{
		String dbName = ip.getAnnotated().getAnnotation(MorphiaDatastore.class).value();
		return createDatastore(dbName);
	}
	
	@Produces @MorphiaAdvancedDatastore
	public AdvancedDatastore createAdvancedDatastore(InjectionPoint ip)
	{
		String dbName = ip.getAnnotated().getAnnotation(MorphiaAdvancedDatastore.class).value();
		return (AdvancedDatastore) createDatastore(dbName);
	}
	
	public Datastore createDatastore(String dbName)
	{
		if(StringUtils.isNotEmpty(dbName))
		{
			return morphia.createDatastore(mongoClient, dbName);
		}
		else
		{
			return null;
		}
	}
}
