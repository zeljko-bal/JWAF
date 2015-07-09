package org.jwaf.agent.management;

import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.agent.AbstractAgent;
import org.jwaf.agent.AgentState;
import org.jwaf.agent.annotations.events.AgentInitializedEvent;
import org.jwaf.agent.exceptions.AgentSelfTerminatedException;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotation.resource.EJBJNDIPrefix;
import org.jwaf.remote.exceptions.AgentTransportSuccessful;

@Stateless
@LocalBean
public class AgentActivator
{
	@Inject @EJBJNDIPrefix
	private String ejbJNDIPrefix;
	
	@Inject
	private AgentRepository agentRepo;
	
	@Inject @AgentInitializedEvent
	private Event<AgentIdentifier> agentInitializedEvent;

	@Asynchronous
	public void activate(AgentIdentifier aid, ACLMessage message)
	{
		// activate agent and get previous state
		String prevState = agentRepo.activate(aid, message);
		
		// agent not yet initialized
		if(AgentState.INITIALIZING.equals(prevState))
		{
			// TODO logger AgentState.INITIALIZING
			return;
		}

		// if agent was passive activate him
		if(AgentState.PASSIVE.equals(prevState))
		{
			String agentTypeName = agentRepo.findView(aid.getName()).getType().getName();
			
			execute(aid, agentTypeName);
		}
	}
	
	private void execute(AgentIdentifier aid, String type)
	{
		System.out.println("[AgentActivator] activating agent: <"+aid.getName()+">");

		try
		{
			// find agent by type
			AbstractAgent agentBean = findAgent(type);

			boolean done = false;

			// while execution is not done (agent not passivated)
			while(!done)
			{
				// execute
				agentBean._execute(aid);

				// try to passivate
				done = agentRepo.passivate(aid, false);
			}
		}
		catch (NamingException e) 
		{
			// TODO agent not found
			// agent bean not found
			e.printStackTrace();
			// force agent to passivate
			agentRepo.passivate(aid, true);
		}
		catch(Exception e)
		{
			Throwable cause = e.getCause();
			
			if(cause instanceof AgentSelfTerminatedException || cause instanceof AgentTransportSuccessful)
			{
				System.out.println("[AgentActivator] "+cause.getMessage());
			}
			else
			{
				e.printStackTrace();
				// if service submit failed force agent to passivate
				agentRepo.passivate(aid, true);
			}
		}
	}
	
	public void setup(AgentIdentifier aid, String type)
	{
		try
		{
			// find agent by type
			AbstractAgent agentBean = findAgent(type);
			
			// invoke initial setup
			agentBean._setup(aid);
		}
		catch (NamingException e) 
		{
			// TODO agent not found
			// agent not found
			e.printStackTrace();
		}
		finally
		{
			// force agent to passivate
			agentRepo.passivate(aid, true);
			
			// notify that agent is initialized
			agentInitializedEvent.fire(aid);
		}
	}
	
	@Asynchronous
	public void onArrival(AgentIdentifier aid, String type)
	{
		try
		{
			// find agent by type
			AbstractAgent agentBean = findAgent(type);
			
			// invoke onArrival
			agentBean._onArrival(aid);
		}
		catch (NamingException e) 
		{
			// TODO agent not found
			// agent not found
			e.printStackTrace();
		}
		finally
		{
			// force agent to passivate
			agentRepo.passivate(aid, true);
			
			// notify that agent has arrived
			// TODO agentArrivedEvent.fire(aid);
		}
	}

	private AbstractAgent findAgent(String type) throws NamingException
	{
		return (AbstractAgent)(new InitialContext()).lookup(ejbJNDIPrefix + type);
	}
}
