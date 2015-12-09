package org.jwaf.common.data.mongo;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

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

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

@Singleton
@LocalBean
@Startup
public class MongoProducer
{
	public static final String SYSTEM_DB_NAME = "jwaf_system";
	
	private MongoClient mongoClient;
	private Morphia morphia;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void setup()
	{
		Document properties = PropertiesUtils.getJsonProperties("mongo_config.json");
		
		List<Document> addressesDoc = properties.get("addresses", List.class);
		List<Document> credentialsDoc = properties.get("credentials", List.class);
		
		List<ServerAddress> addresses = addressesDoc.stream()
				.map(a->new ServerAddress(a.getString("host"), a.getInteger("port")))
				.collect(Collectors.toList());
		
		List<MongoCredential> credentials = credentialsDoc.stream()
				.map(c->MongoCredential.createMongoCRCredential(c.getString("username"), 
																c.getString("db"), 
																c.getString("password").toCharArray()))
				.collect(Collectors.toList());
		
		mongoClient = new MongoClient(addresses, credentials);
		
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
		
		return morphia.createDatastore(mongoClient, dbName);
	}
}
