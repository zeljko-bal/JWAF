package org.jwaf.common.data;

import java.util.Map;

public interface AgentDataRepository
{
	Map<String, String> getData(String agentName);
	String put(String agentName, String key, String value);
	String remove(String agentName, Object key);
	void putAll(String agentName, Map<? extends String, ? extends String> m);
	void clear(String agentName);
}
