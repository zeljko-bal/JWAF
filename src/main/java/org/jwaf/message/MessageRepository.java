package org.jwaf.message;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

	public void persist(ACLMessage message)
	{
		// replace all aid references with managed ones
		message.setSender(manageAID(message.getSender()));

		List<AgentIdentifier> managedAids = new ArrayList<>();

		// add managed versions to managedAids
		message.getReceiverList().forEach((AgentIdentifier aid)->managedAids.add(manageAID(aid)));
		// replace receiver list with managedAids
		message.getReceiverList().clear();
		message.getReceiverList().addAll(managedAids);
		
		// add managed versions to managedAids
		managedAids.clear();
		message.getIn_reply_toList().forEach((AgentIdentifier aid)->managedAids.add(manageAID(aid)));
		// replace in_reply_to list with managedAids
		message.getIn_reply_toList().clear();
		message.getIn_reply_toList().addAll(managedAids);

		// persist
		em.persist(message);
	}

	public void remove(ACLMessage message)
	{
		em.remove(message);
		
		// TODO fire messageRemovedEvent to delete orphan aids
	}

	private AgentIdentifier manageAID(AgentIdentifier aid)
	{		
		// if aid is null
		if(aid == null)
		{
			// nothing to persist
			return null;
		}

		if(aid.getName() != null)
		{
			// if aid with same name is already persistant return
			List<AgentIdentifier> results = em.createQuery("SELECT a FROM AgentIdentifier a WHERE a.name like :name", AgentIdentifier.class)
					.setParameter("name", aid.getName())
					.getResultList();
			if(!results.isEmpty())
			{
				return results.get(0);
			}
		}
		else
		{
			// TODO throw or log, name cant be null
			return null;
		}

		// else persist aid
		em.persist(aid);
		return aid;
	}
}
