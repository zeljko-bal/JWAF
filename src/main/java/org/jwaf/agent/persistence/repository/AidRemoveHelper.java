package org.jwaf.agent.persistence.repository;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.platform.annotation.resource.LocalPlatformName;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class AidRemoveHelper
{
	@Resource
	UserTransaction tx;
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	public void removeIfUnused(AgentIdentifier aid)
	{
		if(localPlatformName.equals(aid.getName()))
		{
			return;
		}
		
		try
		{
			tx.begin();
			AgentIdentifier toRemove = em.find(AgentIdentifier.class, aid.getName());
			em.remove(toRemove);
			tx.commit();
		}
		catch (NotSupportedException | SystemException | SecurityException | IllegalStateException | 
				RollbackException | HeuristicMixedException | HeuristicRollbackException e)
		{}
	}
}
