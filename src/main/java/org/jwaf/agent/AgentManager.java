package org.jwaf.agent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.agent.entity.AgentEntity;
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.agent.entity.AgentType;
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

		// TODO querry by aid
		AgentEntity agent = agentRepo.find(aid);

		// activate agent and get previous state
		prevState = agentRepo.activate(agent, message);

		// if agent was passive activate him
		if(AgentState.PASSIVE.equals(prevState))
		{
			String agentTypeName = agent.getType().getName();

			// execute in a managed thread
			execService.submit(()->
			{
				try
				{
					// find agent by type
					AgentBean agentBean = (AgentBean)(new InitialContext()).lookup(agentJNDIPrefix + agentTypeName);

					// set agents identity
					agentBean.setAid(aid);

					boolean done = false;

					// while execution is not done (agent not passivated)
					while(!done)
					{
						// execute
						agentBean.execute();

						// try to passivate
						done = agentRepo.passivate(agent);
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
					agentRepo.passivate(agent, true);
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
	
	public AgentType getType(AgentIdentifier aid)
	{
		return agentRepo.find(aid).getType();
	}
	
	public Map<String, Serializable> getPrivateData(AgentIdentifier aid)
	{
		return agentRepo.find(aid).getPrivateData();
	}
	
	public void putPrivateData(AgentIdentifier aid, String key, Serializable value)
	{
		AgentEntity agent = agentRepo.find(aid);
		agent.getPrivateData().put(key, value);
		agentRepo.merge(agent);
	}
	
	public void removePrivateData(AgentIdentifier aid, String key)
	{
		AgentEntity agent = agentRepo.find(aid);
		agent.getPrivateData().remove(key);
		agentRepo.merge(agent);
	}

	public Map<String, Serializable> getPublicData(AgentIdentifier aid) 
	{
		return agentRepo.find(aid).getPublicData();
	}
	
	public void putPublicData(AgentIdentifier aid, String key, Serializable value)
	{
		AgentEntity agent = agentRepo.find(aid);
		agent.getPublicData().put(key, value);
		agentRepo.merge(agent);
	}
	
	public void removePublicData(AgentIdentifier aid, String key)
	{
		AgentEntity agent = agentRepo.find(aid);
		agent.getPublicData().remove(key);
		agentRepo.merge(agent);
	}
	
	public String getState(AgentIdentifier aid) 
	{
		return agentRepo.find(aid).getState();
	}
	
	public boolean hasNewMessages(AgentIdentifier aid) 
	{
		return agentRepo.find(aid).hasNewMessages();
	}
}
