package org.jwaf.data.management;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.data.persistence.entity.AgentDataType;
import org.jwaf.data.persistence.repository.AgentDataRepository;
import org.jwaf.data.persistence.repository.DataStore;
import org.jwaf.data.persistence.repository.DataStoreSerialization;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Stateless
@LocalBean
public class AgentDataManager
{
	private static final String PRIVATE = "PRIVATE";
	private static final String PUBLIC = "PUBLIC";
	
	@Inject
	private AgentDataRepository dataRepo;
	
	public DataStore getDataStore(String agentName, AgentDataType dataType)
	{
		if(!dataRepo.containsDataFor(agentName))
		{
			dataRepo.initializeData(agentName);
		}
		
		return new DataStore(dataRepo, dataType, agentName);
	}
	
	public void initializeData(String agentName)
	{
		dataRepo.initializeData(agentName);
	}
	
	public void initializeData(String agentName, String serializedData)
	{
		DataStore privateDataStore = getDataStore(agentName, AgentDataType.PRIVATE);
		DataStore publicDataStore = getDataStore(agentName, AgentDataType.PUBLIC);
		
		JsonObject data = new JsonParser().parse(serializedData).getAsJsonObject();
		
		DataStoreSerialization.deserialize(privateDataStore, data.get(PRIVATE).getAsString());
		DataStoreSerialization.deserialize(publicDataStore, data.get(PUBLIC).getAsString());
	}
	
	public String getAllDataAsString(String agentName)
	{
		DataStore privateDataStore = getDataStore(agentName, AgentDataType.PRIVATE);
		DataStore publicDataStore = getDataStore(agentName, AgentDataType.PUBLIC);
		
		JsonObject jsonData = new JsonObject();
		jsonData.addProperty(PRIVATE, DataStoreSerialization.serialize(privateDataStore));
		jsonData.addProperty(PUBLIC, DataStoreSerialization.serialize(publicDataStore));
		
		return jsonData.getAsString();
	}
}
