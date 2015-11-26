package org.jwaf.common.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

public class SQLQuerryUtils
{
	public static Query createParameterMapQuery(Map<String, String> parameters, String tableName, String mapName, EntityManager em)
	{
		return createParameterMapQuery(parameters, tableName, null, mapName, em);
	}
	
	public static Query createParameterMapQuery(Map<String, String> parameters, String tableName, String attribute, String mapName, EntityManager em)
	{
		StringBuilder queryString = new StringBuilder("SELECT a");
		
		if(attribute != null)
		{
			queryString.append(".").append(attribute);
		}
		
		queryString.append(" FROM ").append(tableName).append(" a");
		
		// add joins
		for(int i=0;i<parameters.size();i++)
		{
			queryString.append(" INNER JOIN a.").append(mapName).append(" m").append(i);
		}
		
		queryString.append(" WHERE");
		
		// add key value clauses
		for(int i=0;i<parameters.size();i++)
		{
			if(i>0)
			{
				queryString.append(" AND");
			}
			
			queryString.append(" KEY(m").append(i).append(") = :key").append(i).append(" AND VALUE(m").append(i).append(") = :val").append(i);
		}
		
		Query query = em.createQuery(queryString.toString(), AgentIdentifier.class);
		
		// set query parameters
		Iterator<Entry<String,String>> iter = parameters.entrySet().iterator();
		for(int i=0;i<parameters.entrySet().size();i++)
		{
			Entry<String, String> param = iter.next();
			query.setParameter("key"+i, param.getKey());
			query.setParameter("val"+i, param.getValue());
		}
		
		return query;
	}
}
