package org.jwaf.agent;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.message.entity.ACLMessage;

/**
 * Session Bean implementation class AgentManager
 */
@Stateless
@LocalBean
public class AgentManager 
{
	@Inject
	AgentRepository agentRepo;
	
	public void receiveMessage(AgentIdentifier aid, ACLMessage message)
	{
		String prevState = agentRepo.activate(aid);		
		
		agentRepo.deliverMessage(aid, message);
		
		if(AgentState.PASSIVE.equals(prevState))
		{
			// jndi lookup
			// run with thread factory
		}
	}
	
	
	
	
}
