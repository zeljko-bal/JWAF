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
import org.jwaf.message.persistence.entity.OutboxEntry;

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
	}
	
	// TODO messageReceivedEventHandler
	public void messageReceivedEventHandler(ACLMessage message)
	{
		if(message.getUnreadCount() <= 0)
		{
			remove(message);
			// TODO fire messageRemovedEvent to delete orphan aids
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createOutboxEntry(String receiverName, ACLMessage message)
	{
		em.persist(new OutboxEntry(receiverName, message));
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ACLMessage retrieveOutboxMessage(String receiverName)
	{
		OutboxEntry entry = em.createQuery("SELECT e FROM OutboxEntry e WHERE e.receiverName LIKE :name", OutboxEntry.class).setParameter("name", receiverName).getSingleResult();
		
		em.remove(entry);
		
		ACLMessage message = entry.getMessage();
		
		/*fireMessageRetrievedEvent(message);*/
		
		return message;
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

