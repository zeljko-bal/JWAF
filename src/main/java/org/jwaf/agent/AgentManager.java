package org.jwaf.agent;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.agent.entity.AgentType;
import org.jwaf.agent.persistence.AgentRepository;
import org.jwaf.agent.persistence.DataStore;
import org.jwaf.agent.persistence.DataStoreType;
import org.jwaf.message.entity.ACLMessage;

/**
 * Session Bean implementation class AgentManager
 */
@Stateless
@LocalBean
public class AgentManager 
{
	private final String agentJNDIPrefix = "java:global/jwaf/";

	@Resource(name="java:comp/DefaultManagedExecutorService")
	ManagedExecutorService execService;

	@Inject
	AgentRepository agentRepo;

	public void deliverMessage(AgentIdentifier aid, ACLMessage message)
	{
		String prevState = null;

		// activate agent and get previous state
		prevState = agentRepo.activate(aid, message);

		// if agent was passive activate him
		if(AgentState.PASSIVE.equals(prevState))
		{
			String agentTypeName = find(aid).getType().getName();

			// execute in a managed thread
			execService.submit(()->
			{
				System.out.println("executing agent activation!!");
				
				try
				{
					// find agent by type
					AgentBean agentBean = (AgentBean)(new InitialContext()).lookup(agentJNDIPrefix + agentTypeName);

					// set agents identity
					// TODO with reflection
					agentBean.setAid(aid);

					boolean done = false;

					// while execution is not done (agent not passivated)
					while(!done)
					{
						// execute
						agentBean.execute();

						// try to passivate
						done = agentRepo.passivate(aid);
					}
				}
				catch (NamingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					// force agent to passivate
					agentRepo.passivate(aid, true);
					throw e;
				}
			});
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
	
	public AgentEntityView find(String name)
	{
		return agentRepo.findView(name);
	}
	
	public AgentEntityView find(AgentIdentifier aid)
	{
		return find(aid.getName());
	}
}
