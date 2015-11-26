package org.jwaf.agent.management;

import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.agent.Agent;
import org.jwaf.agent.AgentState;
import org.jwaf.agent.MultiThreadedAgent;
import org.jwaf.agent.SingleThreadedAgent;
import org.jwaf.agent.annotations.events.AgentInitializedEvent;
import org.jwaf.agent.exceptions.AgentNotFound;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.common.exceptions.AgentSuccessException;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotations.resource.EJBJNDIPrefix;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class AgentActivator
{
	@Inject @EJBJNDIPrefix
	private String ejbJNDIPrefix;
	
	@Inject
	private AgentRepository agentRepo;
	
	@Inject
	private MessageSender messageSender;
	
	@Inject @AgentInitializedEvent
	private Event<AgentIdentifier> agentInitializedEvent;
	
	@Inject
	private Logger log;

	@Asynchronous
	public void activate(AgentIdentifier aid, ACLMessage message)
	{
		try
		{
			AgentType type = agentRepo.findView(aid.getName()).getType();
			
			// find agent by type
			Agent agentBean = findAgent(type.getName());
			
			if(agentBean instanceof SingleThreadedAgent)
			{
				// activate agent and get previous state
				String prevState = agentRepo.activate(aid, message);
				
				if(AgentState.INITIALIZING.equals(prevState))
				{
					// agent not yet initialized
					log.warn("agent: <{}> received a message during initislization.", aid.getName());
					return;
				}
				else if(AgentState.PASSIVE.equals(prevState))
				{
					// if agent was passive activate him
					log.info("activating agent: <{}>", aid.getName());
					
					SingleThreadedAgent stAgent = (SingleThreadedAgent)agentBean;
					boolean done = false;

					// while execution is not done (agent not passivated)
					while(!done)
					{
						// execute
						stAgent._execute(aid);

						// try to passivate
						done = agentRepo.passivate(aid.getName(), false);
					}
				}
			}
			else if(agentBean instanceof MultiThreadedAgent)// AgentType.MULTI_THREADED
			{
				MultiThreadedAgent mtAgent = (MultiThreadedAgent)agentBean;
				mtAgent._handle(aid, message);
				// TODO instance counting, INITIALIZING state, Transit state
			}
			else
			{
				log.error("Agent bean must implement either SingleThreadedAgent or MultiThreadedAgent.");
			}
		}
		catch(AgentNotFound e)
		{
			resendMessage(aid, message);
		}
		catch(Exception e)
		{
			Throwable cause = e.getCause();
			
			if(cause instanceof AgentSuccessException)
			{
				log.info(cause.getMessage());
			}
			else
			{
				log.error("Error during agent execution.", e);
				// if service submit failed force agent to passivate
				agentRepo.passivate(aid.getName(), true);
			}
		}
	}

	public void setup(AgentIdentifier aid, String type)
	{
		try
		{
			// find agent by type
			Agent agentBean = findAgent(type);
			
			// invoke initial setup
			agentBean._setup(aid);
		}
		finally
		{
			// force agent to passivate
			agentRepo.passivate(aid.getName(), true);
			
			// notify that agent is initialized
			agentInitializedEvent.fire(aid);
		}
	}
	
	@Asynchronous
	public void onArrival(AgentIdentifier aid, String type, String data)
	{
		try
		{
			// find agent by type
			Agent agentBean = findAgent(type);
			
			// invoke onArrival
			agentBean._onArrival(aid, data);
		}
		catch(Exception e)
		{
			log.error("Agent <"+aid.getName()+"> did not arrive properly.", e);
			throw e;
		}
	}

	private Agent findAgent(String type)
	{
		try
		{
			return (Agent)(new InitialContext()).lookup(ejbJNDIPrefix + type);
		}
		catch (NamingException e) 
		{
			// agent bean not found
			log.error("Agent bean not found.", e);
			throw new RuntimeException("Agent bean not found.");
		}
	}
	
	private void resendMessage(AgentIdentifier aid, ACLMessage message)
	{
		aid.getAddresses().clear();
		message.getReceiverList().clear();
		message.getReceiverList().add(aid);
		
		messageSender.send(message);
	}
}
