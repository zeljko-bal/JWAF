package org.jwaf.agent.persistence.repository;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.util.SQLQuerryUtils;

@Stateless
@LocalBean
public class AidRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private AidRemoveHelper remover;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<AgentIdentifier> find(Map<String, String> userDefinedParameters)
	{
		return SQLQuerryUtils.createParameterMapQuery(userDefinedParameters, "AgentIdentifier", "data.userDefinedParameters", em).getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AgentIdentifier find(String name)
	{
		return em.find(AgentIdentifier.class, name);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<AgentIdentifier> findAll()
	{
		return em.createQuery("SELECT a FROM AgentIdentifier a", AgentIdentifier.class).getResultList();
	}
	
	public void cleanUp()
	{
		List<AgentIdentifier> agentIdentifiers = findAll();

		agentIdentifiers.forEach(aid -> remover.removeIfUnused(aid) );
	}
}
