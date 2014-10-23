package org.jwaf.agent.management;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.persistence.entity.AgentEntityView;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.agent.persistence.repository.DataStore;
import org.jwaf.agent.persistence.repository.DataStoreType;
import org.jwaf.message.persistence.entity.ACLMessage;

/**
 * Session Bean implementation class AgentManager
 */
@Stateless
@LocalBean
public class AgentManager 
{
	@Inject
	private AgentRepository agentRepo;
	
	@Inject
	AgentExecutor executor;

	public void deliverMessage(AgentIdentifier aid, ACLMessage message)
	{
		String prevState = null;

		// activate agent and get previous state
		prevState = agentRepo.activate(aid, message);

		// if agent was passive activate him
		if(AgentState.PASSIVE.equals(prevState))
		{
			String agentTypeName = find(aid).getType().getName();

			// execute asynchronously
			try
			{
				//execService.submit(new AgentExec(aid, agentTypeName));
				executor.execute(aid, agentTypeName);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				// if service submit failed force agent to passivate
				agentRepo.passivate(aid, true);
			}
		}
	}

	public List<ACLMessage> getMessages(AgentIdentifier aid)
	{
		List<ACLMessage> messages = agentRepo.getMessages(aid);

		// notify that messages have ben retrieved
		// TODO
		messages.forEach((ACLMessage message)->{/*fireMessageRetrievedEvent(message);*/});

		return messages;
	}

	public boolean contains(AgentIdentifier aid)
	{
		return agentRepo.contains(aid);
	}

	public boolean contains(String name)
	{
		return agentRepo.contains(name);
	}

	public AgentType getType(AgentIdentifier aid)
	{
		return find(aid).getType();
	}

	public AgentType getType(String name)
	{
		return find(name).getType();
	}

	public DataStore getDataStore(String name, DataStoreType type)
	{
		return agentRepo.getDataStore(name, type);
	}

	public String getState(AgentIdentifier aid) 
	{
		return find(aid).getState();
	}

	public String getState(String name) 
	{
		return find(name).getState();
	}

	public boolean hasNewMessages(AgentIdentifier aid) 
	{
		return find(aid).hasNewMessages();
	}

	public void ignoreNewMessages(AgentIdentifier aid)
	{
		agentRepo.ignoreNewMessages(aid);
	}

	public AgentEntityView find(String name)
	{
		return agentRepo.findView(name);
	}

	public AgentEntityView find(AgentIdentifier aid)
	{
		return find(aid.getName());
	}
}
