package org.jwaf.agent.management;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
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
import org.jwaf.agent.exceptions.AgentStateChangeFailed;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.common.exceptions.AgentSuccessException;
import org.jwaf.message.annotations.events.MessageRetrievedEvent;
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
	
	@Inject @MessageRetrievedEvent
	private Event<ACLMessage> messageRetrievedEvent;
	
	@Resource(name="java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executorService;
	
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
				SingleThreadedAgent stAgent = (SingleThreadedAgent)agentBean;
				
				// activate agent and get previous state
				boolean activated = agentRepo.activateSingleThreaded(aid.getName(), message);
				
				if(activated)
				{
					executeSingle(aid, stAgent);
				}
			}
			else if(agentBean instanceof MultiThreadedAgent)// AgentType.MULTI_THREADED
			{
				MultiThreadedAgent mtAgent = (MultiThreadedAgent)agentBean;
				
				String currentState = agentRepo.findView(aid.getName()).getState();
				
				if(AgentState.INITIALIZING.equals(currentState))
				{
					// agent not yet initialized
					agentRepo.putToInbox(aid.getName(), message);
				}
				else
				{
					executeMulti(aid, mtAgent, message);
					
					// TODO Transit state
				}
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
		catch(AgentStateChangeFailed e)
		{
			log.error("Error during agent activation.", e);
		}
	}

	public void setup(AgentIdentifier aid, String type)
	{
		Agent agentBean;
		
		try
		{
			// find agent by type
			agentBean = findAgent(type);
			
			// invoke initial setup
			agentBean._setup(aid);
			
			agentRepo.forcePassivate(aid.getName());
			
			// notify that agent is initialized
			agentInitializedEvent.fire(aid);
		}
		catch(Exception e)
		{
			// force agent to passivate
			agentRepo.forcePassivate(aid.getName());
			return;
		}
		
		try
		{
			if(agentBean instanceof SingleThreadedAgent)
			{
				SingleThreadedAgent stAgent = (SingleThreadedAgent)agentBean;
				
				boolean activated = agentRepo.activateSingleThreaded(aid.getName());
				
				if(activated)
				{
					executeSingle(aid, stAgent);
				}
			}
			else if(agentBean instanceof MultiThreadedAgent)
			{
				MultiThreadedAgent mtAgent = (MultiThreadedAgent)agentBean;
				
				for(ACLMessage msg : agentRepo.retrieveFromInbox(aid.getName()))
				{
					// handle every message asynchronously
					executorService.execute(()->executeMulti(aid, mtAgent, msg));
				}
			}
			else
			{
				log.error("Agent bean must implement either SingleThreadedAgent or MultiThreadedAgent.");
			}
		}
		catch(AgentStateChangeFailed e)
		{
			log.error("Error during agent activation.", e);
		}
	}
	
	private void executeSingle(AgentIdentifier aid, SingleThreadedAgent stAgent)
	{
		// if agent was passive activate him
		log.info("activating agent: <{}>", aid.getName());
		
		boolean done = false;
		
		// while execution is not done (agent not passivated)
		while(!done)
		{
			// execute
			try
			{
				stAgent._execute(aid);
			}
			catch (Exception e)
			{
				onExecutionException(aid, e);
				return;
			}
			
			// try to passivate
			done = agentRepo.passivateSingleThreaded(aid.getName());
		}
	}

	private void executeMulti(AgentIdentifier aid, MultiThreadedAgent mtAgent, ACLMessage message)
	{
		agentRepo.activateMultiThreadedInstance(aid.getName());
		
		messageRetrievedEvent.fire(message);
		
		try
		{
			mtAgent._handle(aid, message);
		}
		catch (Exception e)
		{
			onExecutionException(aid, e);
		}
		
		agentRepo.deactivateMultiThreadedInstance(aid.getName());
	}
	
	private void onExecutionException(AgentIdentifier aid, Exception e)
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
			agentRepo.forcePassivate(aid.getName());
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
