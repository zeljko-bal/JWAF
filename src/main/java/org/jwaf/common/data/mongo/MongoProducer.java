package org.jwaf.common.data.mongo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.jwaf.common.data.mongo.annotations.MongoDB;
import org.jwaf.common.data.mongo.annotations.MorphiaAdvancedDatastore;
import org.jwaf.common.data.mongo.annotations.MorphiaDatastore;
import org.jwaf.common.data.mongo.annotations.MorphiaODM;
import org.jwaf.common.data.mongo.annotations.SystemMongoClient;
import org.jwaf.common.util.PropertiesUtils;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * A bean that produces MongoDB and Morphia related objects.
 * 
 * @author zeljko.bal
 */
@Singleton
@LocalBean
@Startup
public class MongoProducer
{
	public static final String SYSTEM_DB_NAME = "jwaf_system";
	
	private MongoClient mongoClient;
	private Morphia morphia;
	
	@Inject
	private Logger logger;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void setup()
	{
		try
		{
			Document properties = PropertiesUtils.getJsonProperties("mongo_config.json");
			
			List<Document> addressesDoc = properties.get("addresses", List.class);
			List<Document> credentialsDoc = properties.get("credentials", List.class);
			
			List<ServerAddress> addresses = addressesDoc.stream()
					.map(a->new ServerAddress(a.getString("host"), a.getInteger("port")))
					.collect(Collectors.toList());
			
			// if using credentials
			if(credentialsDoc != null)
			{
				List<MongoCredential> credentials = credentialsDoc.stream()
						.map(c->MongoCredential.createMongoCRCredential(c.getString("username"), 
																		c.getString("db"), 
																		c.getString("password").toCharArray()))
						.collect(Collectors.toList());
				
				mongoClient = new MongoClient(addresses, credentials);
			}
			else
			{
				mongoClient = new MongoClient(addresses);
			}
			
			morphia = new Morphia();
		}
		catch (IOException e)
		{
			logger.error("Error while loading mongo_config.json", e);
		}
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
		if(StringUtils.isEmpty(dbName))
		{
			dbName = SYSTEM_DB_NAME;
		}
		
		return mongoClient.getDatabase(dbName);
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
		if(StringUtils.isEmpty(dbName))
		{
			dbName = SYSTEM_DB_NAME;
		}
		else
		{
			throw new RuntimeException("Different databases not supported for now, "
					+ "due to issues in org.mongodb.morphia, see: https://github.com/mongodb/morphia/issues/757");
		}
		
		return morphia.createDatastore(mongoClient, dbName);
	}
}
