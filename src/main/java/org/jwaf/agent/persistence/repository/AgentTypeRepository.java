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
import javax.persistence.Query;

import org.jwaf.agent.persistence.entity.AgentType;

@Stateless
@LocalBean
public class AgentTypeRepository
{
	@PersistenceContext
	private EntityManager em;
	
	public AgentType find(String name) throws NoResultException
	{
		return em.find(AgentType.class, name);
	}
	
	public List<AgentType> find(Map<String, String> attributes)
	{
		// create query string
		StringBuilder queryString = new StringBuilder("SELECT t FROM AgentType t JOIN t.attributes a WHERE");
		for(int i=0;i<attributes.keySet().size();i++)
		{
			if(i>0)
			{
				queryString.append(" AND");
			}
			queryString.append(" KEY(a) = :key_").append(i).append(" AND VALUE(a) = :value_").append(i);
		}
		
		// create query
		Query query = em.createQuery(queryString.toString());
		
		// set query parameters
		int i=0;
		for(String key : attributes.keySet())
		{
			query.setParameter("key_"+i, key).setParameter("value_"+i, attributes.get(key));
			i++;
		}
		
		// http://stackoverflow.com/questions/21263725/jpas-mapkey-value-query-by-jpql-failed
		// TODO List<AgentType> find(Map<String, String> attributes)
		return null;
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
