package org.jwaf.agent;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.entity.AgentEntity;
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.message.entity.ACLMessage;

/**
 * Session Bean implementation class AgentRepository
 */
@Stateless
@LocalBean
public class AgentRepository 
{
	@PersistenceContext
	EntityManager em;

	public String activate(AgentIdentifier aid)
	{
		AgentEntity agent = em.find(AgentEntity.class, 2, LockModeType.PESSIMISTIC_WRITE);

		// get previous state
		String prevState = agent.getState();

		// activate agent
		agent.setState(AgentState.ACTIVE);

		// set unread messages flag
		agent.setHasNewMessages(true);

		em.merge(agent);

		return prevState;
	}

	public boolean passivate(AgentIdentifier aid)
	{
		AgentEntity agent = em.find(AgentEntity.class, 2, LockModeType.PESSIMISTIC_WRITE);

		// if agent has new messages
		if(agent.hasNewMessages())
		{
			// set flag to false and dont passivate
			agent.setHasNewMessages(false);

			em.merge(agent);

			// not passivated
			return false;
		}
		else
		{
			// else do passivate
			agent.setState(AgentState.PASSIVE);

			em.merge(agent);

			// passivated
			return true;
		}
	}

	public void deliverMessage(AgentIdentifier aid, ACLMessage message)
	{
		AgentEntity agent = em.find(AgentEntity.class, 2, LockModeType.PESSIMISTIC_WRITE);

		agent.getMessages().add(message);

		em.merge(agent);
	}
}
