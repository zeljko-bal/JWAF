package org.jwaf.service.persistence.repository;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.service.persistence.entity.AgentServiceType;
import org.jwaf.util.SQLQuerryUtil;

@Stateless
@LocalBean
public class AgentServiceRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AgentServiceType find(String name)
	{
		return em.find(AgentServiceType.class, name, LockModeType.PESSIMISTIC_WRITE);
	}
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<String> find(Map<String, String> attributes)
	{
		return SQLQuerryUtil.createParameterMapQuery(attributes, "AgentServiceType", "name", "attributes", em).getResultList();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void register(AgentServiceType service)
	{
		AgentServiceType existingType = find(service.getName());
		if(existingType == null)
		{
			em.persist(service);
		}
		else
		{
			existingType.getAttributes().clear();
			existingType.getAttributes().putAll(service.getAttributes());
			em.merge(existingType);
		}
	}
}
