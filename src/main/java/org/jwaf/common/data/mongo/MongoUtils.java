package org.jwaf.common.data.mongo;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public class MongoUtils
{
	public static <T> List<T> findAndDeleteAll(Query<T> query, Datastore ds)
	{
		List<T> ret = new ArrayList<>();
		long count = query.countAll(); // ensure that the loop isn't infinite
		
		for(int i=0; i<count; i++)
		{
			T result = ds.findAndDelete(query);
			if(result == null) break;
			ret.add(result);
		}
		
		return ret;
	}
}
