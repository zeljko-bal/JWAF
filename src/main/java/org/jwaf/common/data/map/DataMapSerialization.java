package org.jwaf.common.data.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A utility class for serializing / deserializing a {@link DataMap} to json.
 * 
 * @author zeljko.bal
 */
public class DataMapSerialization
{
	public static String serialize(DataMap dataStore)
	{
		JsonObject data = new JsonObject();
		
		for(String key : dataStore.keySet())
		{
			data.addProperty(key, dataStore.get(key));
		}
		
		return data.getAsString();
	}
	
	public static void deserialize(DataMap dataStore, String serialized)
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
