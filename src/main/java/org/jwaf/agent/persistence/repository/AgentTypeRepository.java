package org.jwaf.agent.persistence.repository;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	@SuppressWarnings("unchecked")
	public List<AgentType> find(Map<String, String> attributes)
	{
		StringBuilder queryString = new StringBuilder("SELECT t FROM AgentType t WHERE");
		
		// create query string
		for(int i=0;i<attributes.entrySet().size();i++)
		{
			if(i>0)
			{
				queryString.append(" AND");
			}
			
			queryString.append(" :ent_").append(i).append(" MEMBER OF t.attributes");
		}
		
		// create query
		Query query = em.createQuery(queryString.toString(), AgentType.class);
		
		// set query parameters
		Iterator<Entry<String,String>> iter = attributes.entrySet().iterator();
		for(int i=0;i<attributes.entrySet().size();i++)
		{
			query.setParameter("ent_"+i, iter.next());
		}
		
		return query.getResultList();
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
