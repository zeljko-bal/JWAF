package org.jwaf.message;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.AgentRepository;
import org.jwaf.agent.entity.AgentIdentifier;
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
    	
		message.setSender(agentRepo.manageAID(message.getSender()));

		List<AgentIdentifier> managedAids = new ArrayList<>();

		// add managed versions to managedAids
		message.getReceiverList().forEach((AgentIdentifier aid)->managedAids.add(agentRepo.manageAID(aid)));
		// replace receiver list with managedAids
		message.getReceiverList().clear();
		message.getReceiverList().addAll(managedAids);
		
		// add managed versions to managedAids
		managedAids.clear();
		message.getIn_reply_toList().forEach((AgentIdentifier aid)->managedAids.add(agentRepo.manageAID(aid)));
		// replace in_reply_to list with managedAids
		message.getIn_reply_toList().clear();
		message.getIn_reply_toList().addAll(managedAids);
	}
}
