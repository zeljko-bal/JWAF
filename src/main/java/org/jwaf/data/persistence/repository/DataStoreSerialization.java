package org.jwaf.data.persistence.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataStoreSerialization
{
	public static String serialize(DataStore dataStore)
	{
		JsonObject data = new JsonObject();
		
		for(String key : dataStore.keySet())
		{
			data.addProperty(key, dataStore.get(key));
		}
		
		return data.getAsString();
	}
	
	public static void deserialize(DataStore dataStore, String serialized)
	{
		JsonObject data = new JsonParser().parse(serialized).getAsJsonObject();
		
		Map<String, String> dataMap = new HashMap<>();
		
		for(Entry<String, JsonElement> entry : data.entrySet())
		{
			dataMap.put(entry.getKey(), entry.getValue().getAsString());
		}
		
		dataStore.putAll(dataMap);
	}
}
