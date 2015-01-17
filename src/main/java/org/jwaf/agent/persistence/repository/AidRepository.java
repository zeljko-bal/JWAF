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
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Stateless
@LocalBean
public class AidRepository
{
	@PersistenceContext
	private EntityManager em;
	
	public AgentIdentifier manageAID(AgentIdentifier aid)
	{
		return manageAID(aid, false);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AgentIdentifier manageAID(AgentIdentifier aid, boolean update)
	{
		// if aid is null
		if(aid == null)
		{
			// nothing to manage
			return null;
		}

		if(aid.getName() != null)
		{
			AgentIdentifier existentAid = em.find(AgentIdentifier.class, aid.getName(), LockModeType.PESSIMISTIC_WRITE);
			
			// if aid with same name already is persistant return
			if(existentAid != null)
			{
				if(update)
				{
					existentAid.getAddresses().addAll(aid.getAddresses());
					existentAid.getUserDefinedParameters().putAll(aid.getUserDefinedParameters());
					existentAid.getResolvers().addAll(aid.getResolvers());
					em.merge(existentAid);
					em.flush();
				}
				
				return existentAid;
			}
			else // persist aid
			{
				// manage resolvers recursively
				aid.getResolvers().replaceAll((AgentIdentifier res) -> manageAID(res, update));
				
				em.persist(aid);
				em.flush();
				return aid;
			}
		}
		else
		{
			throw new NullPointerException("[AgentRepository#manageAID] Agent name cannot be null.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AgentIdentifier> find(Map<String, String> publicData)
	{
		StringBuilder queryString = new StringBuilder("SELECT a FROM AgentIdentifier a WHERE");
		
		// create query string
		for(int i=0;i<publicData.entrySet().size();i++)
		{
			if(i>0)
			{
				queryString.append(" AND");
			}
			
			queryString.append(" :ent_").append(i).append(" MEMBER OF a.publicData");
		}
		
		// create query
		Query query = em.createQuery(queryString.toString(), AgentIdentifier.class);
		
		// set query parameters
		Iterator<Entry<String,String>> iter = publicData.entrySet().iterator();
		for(int i=0;i<publicData.entrySet().size();i++)
		{
			query.setParameter("ent_"+i, iter.next());
		}
		
		return query.getResultList();
	}
	
	public AgentIdentifier find(String name)
	{
		return em.find(AgentIdentifier.class, name);
	}
	
	public void cleanUp()
	{
		List<AgentIdentifier> agentIdentifiers = em.createQuery("SELECT a FROM AgentIdentifier a", AgentIdentifier.class).getResultList();
		
		agentIdentifiers.forEach(aid -> removeIfUnused(aid));
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void removeIfUnused(AgentIdentifier aid)
	{
		em.lock(aid, LockModeType.PESSIMISTIC_WRITE);
		
		// try to remove aid, if there is a reference to it do nothing
		try
		{
			em.remove(aid);
			em.flush();
		}
		catch(ConstraintViolationException e)
		{/*no-op, still in use*/}
	}
}
