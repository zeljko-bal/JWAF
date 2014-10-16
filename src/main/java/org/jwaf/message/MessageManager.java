package org.jwaf.message;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;

import org.jwaf.agent.entity.AgentEntity;
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.message.entity.ACLMessage;

/**
 * Session Bean implementation class MessageManager
 */
@Stateless
@LocalBean
public class MessageManager 
{
	@Inject
	private MessageRepository repo;
/*
	public void send()
	{
		for(AgentIdentifier agentId : message.getReceiverList())
		{
			sendTo(agentId, message);
		}
	}
	
    private void sendTo(AgentIdentifier agentId, ACLMessage message)
    {
    	AgentEntity agent = em.find(AgentEntity.class, ???, LockModeType.PESSIMISTIC_WRITE);
    	
    	String state = agent.getState();
    	
		agent.getMessages().add(message);
		
		em.merge(agent);
		
		em.
		
		if(AgentState.PASSIVE.equals(state))
    	{
    		
    	}  	
    }
	*/

}
