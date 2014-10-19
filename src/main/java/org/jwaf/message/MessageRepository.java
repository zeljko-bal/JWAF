package org.jwaf.message;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.message.entity.ACLMessage;

/**
 * Session Bean implementation class MessageRepository
 */
@Stateless
@LocalBean
public class MessageRepository 
{
	@PersistenceContext
	EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persist(ACLMessage message)
	{
		em.persist(message);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void remove(ACLMessage message)
	{
		em.remove(message);
		em.flush();
		
		// TODO fire messageRemovedEvent to delete orphan aids
	}
}
