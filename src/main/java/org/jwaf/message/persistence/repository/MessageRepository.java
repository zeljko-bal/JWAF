package org.jwaf.message.persistence.repository;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.annotation.event.AidReferenceDroppedEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.message.annotation.event.MessageRemovedEvent;
import org.jwaf.message.annotation.event.MessageRetrievedEvent;
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
	
	@Inject @MessageRetrievedEvent
	private Event<ACLMessage> messageRetrievedEvent;
	
	@Inject @MessageRemovedEvent
	private Event<ACLMessage> messageRemovedEvent;
	
	@Inject @AidReferenceDroppedEvent
	private Event<AgentIdentifier> aidReferenceDroppedEvent;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persist(ACLMessage message)
	{
		makeAidsManaged(message);
		
		em.persist(message);
	}
	
	public void messageRetrievedEventHandler(@Observes @MessageRetrievedEvent ACLMessage message)
	{
		if(removeUnusedMessage(message))
		{
			messageRemovedEvent.fire(message);
			// drop aid references
			aidReferenceDroppedEvent.fire(message.getSender());
			message.getReceiverList().forEach((AgentIdentifier aid) -> aidReferenceDroppedEvent.fire(aid));
			message.getIn_reply_toList().forEach((AgentIdentifier aid) -> aidReferenceDroppedEvent.fire(aid));
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private boolean removeUnusedMessage(ACLMessage message)
	{
		em.refresh(message, LockModeType.PESSIMISTIC_WRITE);
		
		// decrement unread count
		message.setUnreadCount(message.getUnreadCount()-1);
				
		if(message.getUnreadCount() <= 0)
		{
			em.remove(message);
			
			return true;
		}
		else 
		{
			em.merge(message);
			
			return false;
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createOutboxEntry(String receiverName, ACLMessage message)
	{
		em.persist(new OutboxEntry(receiverName, message));
	}
	
	public ACLMessage retrieveOutboxMessage(String receiverName)
	{		
		ACLMessage message = retrieveOutboxMessageTransactional(receiverName);
		
		messageRetrievedEvent.fire(message);
		
		return message;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private ACLMessage retrieveOutboxMessageTransactional(String receiverName)
	{
		OutboxEntry entry = em.find(OutboxEntry.class, receiverName);
		
		em.remove(entry);
		
		return entry.getMessage();
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

