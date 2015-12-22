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

/**
 * A management bean that contains the logic for invocation of {@link Agent}'s lifecycle methods.
 * 
 * @author zeljko.bal
 */
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
	
	/**
	 * Notifies an agent that a new message has arrived. 
	 * <br><br>
	 * In case of a {@link SingleThreadedAgent} the message is put into the agents inbox 
	 * and the {@link AgentRepository} tries to transition the agent into an ACTIVE state.
	 * If the agent has been activated the {@link SingleThreadedAgent#_execute(AgentIdentifier) _execute} 
	 * method is called repeatedly until the {@link AgentRepository} successfully transitions the agent 
	 * into a PASSIVE state (there are no new messages that agent hasn't been notified about).
	 * <br><br>
	 * In case of a {@link MultiThreadedAgent} the message is passed to the agent directly by calling 
	 * the {@link MultiThreadedAgent#_handle(AgentIdentifier, ACLMessage) _handle} method.
	 * If the agent is in an INITIALIZING state, the message is stored in the agents inbox.
	 * 
	 * @param aid agent to be activated
	 * @param message activation message
	 */
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
				
				// try to transition the agent into an ACTIVE state
				boolean activated = agentRepo.activateSingleThreaded(aid.getName(), message);
				
				if(activated)
				{
					// invoke _execute method
					executeSingleThreaded(aid, stAgent);
				}
			}
			else if(agentBean instanceof MultiThreadedAgent)
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
					// invoke _handle method
					executeMultiThreaded(aid, mtAgent, message);
					
					// TODO handle IN_TRANSIT state
				}
			}
			else
			{
				log.error("Agent bean must implement either SingleThreadedAgent or MultiThreadedAgent.");
			}
		}
		catch(AgentNotFound e)
		{
			// if for some reason agent can't be found on this platform resend the message with only the agent's name
			resendMessage(aid, message);
		}
		catch(AgentStateChangeFailed e)
		{
			log.error("Error during agent activation.", e);
		}
	}
	
	/**
	 * Invokes {@link Agent}'s _setup method after the agent has been created. 
	 * After successful invocation transitions the agent into a PASSIVE state and fires the {@link AgentInitializedEvent}.
	 * Also asynchronously notifies the agent about any messages that arrived before the agent has been initialized.
	 * 
	 * @param aid agent to be initialized
	 * @param type name of the {@link AgentType}
	 */
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
				
				// activate if there already are messages waiting to be processed
				boolean activated = agentRepo.activateSingleThreaded(aid.getName());
				
				if(activated)
				{
					// execute asynchronously
					executorService.execute(()->executeSingleThreaded(aid, stAgent));
				}
			}
			else if(agentBean instanceof MultiThreadedAgent)
			{
				MultiThreadedAgent mtAgent = (MultiThreadedAgent)agentBean;
				
				for(ACLMessage msg : agentRepo.retrieveFromInbox(aid.getName()))
				{
					// handle every message asynchronously
					executorService.execute(()->executeMultiThreaded(aid, mtAgent, msg));
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
	
	/**
	 * Invokes agent's _execute method while there are new messages.
	 * 
	 * @param aid agent identifier
	 * @param stAgent agent bean instance
	 */
	private void executeSingleThreaded(AgentIdentifier aid, SingleThreadedAgent stAgent)
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
	
	/**
	 * Passes the message directly to agent's _handle method.
	 * 
	 * @param aid agent identifier
	 * @param mtAgent agent bean instance
	 * @param message
	 */
	private void executeMultiThreaded(AgentIdentifier aid, MultiThreadedAgent mtAgent, ACLMessage message)
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
		// recursively find if the cause is instance of AgentSuccessException
		Throwable successEx = e;
		while(successEx != null)
		{
			if(successEx instanceof AgentSuccessException) break;
			else successEx = successEx.getCause();
		}
		
		if(successEx != null)
		{
			log.info(successEx.getMessage());
		}
		else
		{
			log.error("Error during agent execution.", e);
			// if service submit failed force agent to passivate
			agentRepo.forcePassivate(aid.getName());
		}
	}
	
	/**
	 * Invokes agent's _onArrival method.
	 * 
	 * @param aid agent that has arrived
	 * @param type type name
	 * @param data serialized data
	 */
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
	
	/**
	 * Sends the message with aid only containing the name and no addresses, 
	 * so that the platform can try to determine the correct address.
	 */
	private void resendMessage(AgentIdentifier aid, ACLMessage message)
	{
		aid.getAddresses().clear();
		message.getReceiverList().clear();
		message.getReceiverList().add(aid);
		
		messageSender.send(message);
	}
}
