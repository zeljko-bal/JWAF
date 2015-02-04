package org.jwaf.agent.persistence.repository;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.util.SQLQuerryUtils;



@Stateless
@LocalBean
public class AgentTypeRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AgentType find(String name) throws NoResultException
	{
		return em.find(AgentType.class, name);
	}
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<AgentType> find(Map<String, String> attributes)
	{
		return SQLQuerryUtils.createParameterMapQuery(attributes, "AgentType", "attributes", em).getResultList();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void create(AgentType type)
	{
		em.persist(type);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void remove(String name)
	{
		em.remove(find(name));
	}
}
