package org.jwaf.message;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.AgentManager;
import org.jwaf.agent.AgentRepository;
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
	private MessageRepository messageRepo;
	
	@Inject
	AgentManager agentManager;
	
	@Inject
	AgentRepository agentRepo;

	// TODO REST web method
	public void handleMessage(ACLMessage message)
	{
		makeAidsManaged(message);
		
		for(AgentIdentifier aid : message.getReceiverList())
		{
			if(agentManager.contains(aid))
			{
				// if agent is local to this platform
				sendToLocal(aid, message);
			}
			/*else if(remoteRepo.containsAgent(aid))
			{
				// if we can locate agent on a remote platform
				// TODO send to remote platform
				sendToRemote(aid, message);
			}*/
			else
			{
				// if the receiver cannot be located leave message in outbox for manual retrieval
				sendToOutbox(aid, message);
			}
		}
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

	private void sendToLocal(AgentIdentifier aid, ACLMessage message)
    {
    	// persist message in local repositoey
    	messageRepo.persist(message);
    	
    	// link messages to agents and notify them
    	agentManager.deliverMessage(aid, message);
    }
    
    private void sendToRemote(AgentIdentifier aid, ACLMessage message)
    {
    	// TODO
    }
    
    private void sendToOutbox(AgentIdentifier aid, ACLMessage message)
    {
    	// TODO
    }
    
    private void messageReceivedEventHandler()
    {
    	// TODO
    	/*if()
    	{
    		messageRepo.remove(message);
    	}*/
    }
}
