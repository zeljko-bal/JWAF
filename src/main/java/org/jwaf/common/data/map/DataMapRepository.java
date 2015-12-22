package org.jwaf.common.data.map;

import java.util.Map;

/**
 * An interface that represents a data repository used in a {@link DataMap}.
 * 
 * @author zeljko.bal
 */
public interface DataMapRepository
{
	Map<String, String> getData();
	String put(String key, String value);
	String remove(Object key);
	void putAll(Map<? extends String, ? extends String> m);
	void clear();
	boolean exists();
}
