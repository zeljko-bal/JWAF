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
import org.jwaf.agent.annotation.event.AgentInitializedEvent;
import org.jwaf.agent.exception.AgentSelfTerminatedException;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotation.resource.EJBJNDIPrefix;

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

			// execute asynchronously
			try
			{
				execute(aid, agentTypeName);
			}
			catch(AgentSelfTerminatedException ex)
			{
				// TODO logger AgentSelfTerminatedException
				System.out.println(ex.getMessage());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				// if service submit failed force agent to passivate
				agentRepo.passivate(aid, true);
			}
		}
	}
	
	private void execute(AgentIdentifier aid, String type)
	{
		System.out.println("executing agent activation of agent: "+aid.getName() + ", of type: " + type);

		try
		{
			// find agent by type
			AbstractAgent agentBean = findAgent(type);

			// set agents identity
			agentBean.setAid(aid);

			boolean done = false;

			// while execution is not done (agent not passivated)
			while(!done)
			{
				// execute
				agentBean.execute();

				// try to passivate
				done = agentRepo.passivate(aid, false);
			}
		}
		catch (NamingException e) 
		{
			// TODO agent not found
			// agent not found
			e.printStackTrace();
			// force agent to passivate
			agentRepo.passivate(aid, true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// force agent to passivate
			agentRepo.passivate(aid, true);
			throw e;
		}
	}
	
	public void setup(AgentIdentifier aid, String type)
	{
		try
		{
			// find agent by type
			AbstractAgent agentBean = findAgent(type);

			// set agents identity
			agentBean.setAid(aid);
			
			// invoke initial setup
			agentBean.setup();
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

			// set agents identity
			agentBean.setAid(aid);
			
			// invoke onArrival
			agentBean.onArrival();
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
