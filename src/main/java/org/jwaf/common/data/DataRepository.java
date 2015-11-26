package org.jwaf.common.data;

import java.util.Map;

public interface DataRepository
{
	Map<String, String> getData();
	String put(String key, String value);
	String remove(Object key);
	void putAll(Map<? extends String, ? extends String> m);
	void clear();
}
