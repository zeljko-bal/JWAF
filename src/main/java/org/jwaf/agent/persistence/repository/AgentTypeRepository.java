package org.jwaf.agent.persistence.repository;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.persistence.entity.AgentType;

@Stateless
@LocalBean
public class AgentTypeRepository
{
	@PersistenceContext
	private EntityManager em;
	
	public AgentType find(String name) throws NoResultException
	{
		return em.createQuery("SELECT t FROM AgentType t WHERE t.name LIKE :name", AgentType.class).setParameter("name", name).getSingleResult();
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
