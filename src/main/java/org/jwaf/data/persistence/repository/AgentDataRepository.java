package org.jwaf.data.persistence.repository;

import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.data.persistence.entity.AgentData;
import org.jwaf.data.persistence.entity.AgentDataType;

@Stateless
@LocalBean
public class AgentDataRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Map<String, String> getData(String agentName, AgentDataType dataType)
	{
		AgentData data = em.find(AgentData.class, agentName);
		if(data == null)
		{
			return null;
		}
		return data.get(dataType);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String put(String agentName, AgentDataType dataType, String key, String value)
	{
		AgentData data = em.find(AgentData.class, agentName);
		
		String ret = data.get(dataType).put(key, value);
		em.merge(data);
		
		return ret;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String remove(String agentName, AgentDataType dataType, Object key)
	{
		AgentData data = em.find(AgentData.class, agentName);
		
		String ret = data.get(dataType).remove(key);
		em.merge(data);
		
		return ret;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void putAll(String agentName, AgentDataType dataType, Map<? extends String, ? extends String> m)
	{
		AgentData data = em.find(AgentData.class, agentName);
		
		data.get(dataType).putAll(m);
		em.merge(data);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void clear(String agentName, AgentDataType dataType)
	{
		AgentData data = em.find(AgentData.class, agentName);
		
		data.get(dataType).clear();
		em.merge(data);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean containsDataFor(String agentName)
	{
		return em.find(AgentData.class, agentName) != null;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void initializeData(String agentName)
	{
		em.persist(new AgentData(agentName));
	}
}
