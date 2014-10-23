package org.jwaf.message.persistence.repository;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.message.persistence.entity.ACLMessage;

/**
 * Session Bean implementation class MessageRepository
 */
@Stateless
@LocalBean
public class MessageRepository 
{
	@PersistenceContext
	EntityManager em;
	
	@Inject
	AgentRepository agentRepo;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persist(ACLMessage message)
	{
		makeAidsManaged(message);
		
		em.persist(message);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void remove(ACLMessage message)
	{
		em.remove(message);
		em.flush();
		
		// TODO fire messageRemovedEvent to delete orphan aids
	}
	
	private void makeAidsManaged(ACLMessage message)
    {
		// replace all aid references with managed ones
		
		// replace sender aid with managedAid
		message.setSender(agentRepo.manageAID(message.getSender()));

		// replace receiver list with managedAids		
		message.getReceiverList().replaceAll((AgentIdentifier aid) -> agentRepo.manageAID(aid) );
		
		// replace in_reply_to list with managedAids		
		message.getIn_reply_toList().replaceAll((AgentIdentifier aid) -> agentRepo.manageAID(aid) );
	}
}
