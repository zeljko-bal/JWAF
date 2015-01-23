package org.jwaf.task.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.task.persistence.entity.TaskResult;

@Stateless
@LocalBean
public class TaskResultRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persist(TaskResult result)
	{
		em.persist(result);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<TaskResult> retrieveResultSet(String employer)
	{
		List<TaskResult> ret = null;
		
		ret = em.createQuery("SELECT r FROM TaskResult r WHERE r.employer = :employer", TaskResult.class).setParameter("employer", employer).getResultList();
		
		ret.forEach(res -> em.remove(res));
		
		return ret;
	}
}
