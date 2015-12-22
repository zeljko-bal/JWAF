package org.jwaf.agent.management;

import java.net.URL;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.jwaf.agent.AgentState;
import org.jwaf.agent.annotations.LocalPlatformAid;
import org.jwaf.agent.annotations.events.AgentCreatedEvent;
import org.jwaf.agent.annotations.events.AgentRemovedEvent;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentEntityView;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.common.util.AgentNameUtils;
import org.jwaf.message.annotations.events.MessageRetrievedEvent;
import org.jwaf.message.management.MessageFinder;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.performative.PlatformPerformative;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotations.resource.LocalPlatformAddress;
import org.jwaf.platform.annotations.resource.LocalPlatformName;
import org.slf4j.Logger;

/**
 * A bean that contains agent directory management methods.
 */
@Stateless
@LocalBean
public class AgentManager 
{
	@Inject
	private AgentRepository agentRepo;
	
	@Inject
	private AidManager aidManager;
	
	@Inject
	private AgentTypeManager typeManager;
	
	@Inject
	private AgentActivator activator;
	
	@Inject
	private MessageSender messageSender;
	
	@Inject
	private MessageFinder messageFinder;
	
	@Inject
	private AgentNameUtils nameUtils;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @AgentCreatedEvent
	private Event<AgentIdentifier> agentCreatedEvent;
	
	@Inject @AgentRemovedEvent
	private Event<AgentIdentifier> agentRemovedEvent;
	
	@Inject @MessageRetrievedEvent
	private Event<ACLMessage> messageRetrievedEvent;
	
	@Inject
	private Logger log;
	
	/**
	 * Initializes an agent based on the provided {@link CreateAgentRequest} and fires {@link AgentCreatedEvent}.
	 * 
	 * @param request containing agent type and additional createion context information
	 * @return created agent's identifier
	 */
	public AgentIdentifier initialize(CreateAgentRequest request)
	{
		AgentIdentifier aid = createAgentEntity(request);
		
		// invoke agent's _setup method
		activator.setup(aid, request.getType());
		
		agentCreatedEvent.fire(aid);
		
		return aid;
	}
	
	/**
	 * Creates the {@link AgentEntity} of requested type and with the requested name if specified. 
	 * If the name is not specified a random name is used.
	 * @see AgentNameUtils 
	 * @see CreateAgentRequest
	 * 
	 * @param request containing agent type and additional createion context information
	 * @return created agent's identifier
	 */
	private AgentIdentifier createAgentEntity(CreateAgentRequest request)
	{
		AgentType type = null;
		
		try
		{
			type = typeManager.find(request.getType());
		}
		catch (NoResultException e)
		{
			log.error("Agent type not found during initialization.", e);
		}
		
		// new aid with name : <random-uuid>:<agent-type>@<local-platform-name>
		String name;
		if(request.getParams().containsKey(CreateAgentRequest.AID_NAME))
		{
			name = request.getParams().get(CreateAgentRequest.AID_NAME);
		}
		else
		{
			name = nameUtils.createRandom(type.getName());
		}
		
		AgentIdentifier aid = new AgentIdentifier(name);
		aid.getAddresses().add(localPlatformAddress);
		aid = aidManager.insert(aid);
		
		AgentEntity newAgent = new AgentEntity(type, aid);
		
		// persist agent
		agentRepo.create(newAgent);
		
		return aid;
	}
	
	/**
	 * Sends a message to the specified agent that requests from the agent to self terminate.
	 * 
	 * @param agentName name of the agent that should self terminate
	 */
	public void requestTermination(String agentName)
	{
		ACLMessage message = new ACLMessage(PlatformPerformative.SELF_TERMINATE, getPlatformAid());
		message.getReceiverList().add(agentRepo.findView(agentName).getAid());
		
		messageSender.send(message);
	}
	
	/*
	 * methods delegated to AgentRepository
	 */
	
	/**
	 * Find an {@link AgentEntityView}. A view of an {@link AgentEntity} that exposes only aid, type and state.
	 * 
	 * @param agentName
	 * @return AgentEntityView
	 */
	public AgentEntityView findView(String agentName)
	{
		return agentRepo.findView(agentName);
	}
	
	/**
	 * Check if an agent has new messages that he wasn't notified about.
	 * 
	 * @param agentName
	 * @return true if new messages are available
	 */
	public boolean hasNewMessages(String agentName)
	{
		return agentRepo.hasNewMessages(agentName);
	}
	
	/**
	 * Returns all messages currently in agent's inbox. The messages are removed from the inbox.
	 * Also fires {@link MessageRetrievedEvent} for all retrieved messages.
	 * 
	 * @param agentName
	 * @return list of retrieved messages
	 */
	public List<ACLMessage> retrieveMessages(String agentName)
	{
		List<ACLMessage> messages = agentRepo.retrieveFromInbox(agentName);
		
		// notify that messages have ben retrieved
		messages.forEach(messageRetrievedEvent::fire);
		
		return messages;
	}
	
	/**
	 * Find a message based on a query creating function. 
	 * Found messages are removed from the inbox and returned as a result.
	 * @see QueryFunction
	 * 
	 * @param agentName
	 * @param queryFunc a function that configures the query
	 * @return a list of retrieved messages
	 */
	public List<ACLMessage> findMessages(String agentName, QueryFunction<ACLMessage> queryFunc)
	{
		// get ids of inbox messages
		List<String> messageIDs = agentRepo.getMessageIDs(agentName);
		
		// find messages based on the query
		List<ACLMessage> messages = messageFinder.find(messageIDs, queryFunc);
		
		// remove messages from inbox
		agentRepo.removeFromInbox(agentName, messages);
		
		// signal that the agent has retrieved the messages
		messages.forEach(messageRetrievedEvent::fire);
		
		return messages;
	}
	
	/**
	 * Ignores all new messages in agent's inbox. Agent will not be notified about them.
	 */
	public void ignoreNewMessages(String agentName)
	{
		agentRepo.ignoreNewMessages(agentName);
	}
	
	/**
	 * Returns the number of messages currently in agent's inbox.
	 * 
	 * @param agentName
	 * @return the number of messages currently in agent's inbox
	 */
	public int getMessagesCount(String agentName)
	{
		return agentRepo.getMessageIDs(agentName).size();
	}
	
	/**
	 * Puts a message back to agent's inbox.
	 */
	public void putBackToInbox(String agentName, ACLMessage message)
	{
		agentRepo.putBackToInbox(agentName, message);
	}
	
	/**
	 * Returns the number of active instances of a given agent.
	 * 
	 * @param agentName
	 * @return number of active instances
	 */
	public Integer getActiveInstances(String agentName)
	{
		return agentRepo.getActiveInstances(agentName);
	}
	
	/**
	 * Check if this platform contains an agent with the given identifier.
	 * 
	 * @param aid agent identifier
	 * @return true if the agent exists
	 */
	public boolean contains(AgentIdentifier aid)
	{
		return agentRepo.containsAgent(aid);
	}
	
	/**
	 * Check if this platform contains an agent with the given name.
	 * 
	 * @param agentName
	 * @return true if the agent exists
	 */
	public boolean contains(String agentName)
	{
		return agentRepo.containsAgent(agentName);
	}
	
	/**
	 * Removes an agent with the given name from the local platform.
	 * Also fires {@link AgentRemovedEvent}.
	 */
	public void remove(String agentName)
	{
		AgentIdentifier removedAid = agentRepo.findView(agentName).getAid();
		
		AgentEntity removedAgent = agentRepo.remove(agentName);
		
		removedAgent.getMessages().forEach(messageRetrievedEvent::fire);
		
		agentRemovedEvent.fire(removedAid);
	}
	
	/*
	 * agent transport methods
	 */
	
	/**
	 * Puts the requested agent in IN_TRANSIT state and returns the {@link AgentEntity}.
	 * 
	 * @param agentName
	 * @return departed AgentEntity
	 */
	public AgentEntity depart(String agentName)
	{
		return agentRepo.depart(agentName);
	}
	
	/**
	 * Persists an agent from a remote platform and  invokes agent's _onArrival method.
	 * 
	 * @param agent that has arrived
	 * @param serializedData data that the agent has serialized for transport
	 * @throws Exception
	 */
	public void receiveAgent(AgentEntity agent, String serializedData) throws Exception
	{
		try
		{
			// persist aid
			AgentIdentifier remoteAid = agent.getAid();
			AgentIdentifier aid = new AgentIdentifier(remoteAid.getName());
			aid.getAddresses().add(localPlatformAddress);
			aid.getResolvers().addAll(remoteAid.getResolvers());
			aid = aidManager.save(aid);
			
			// persist agent entity
			AgentEntity newAgent = new AgentEntity();
			newAgent.setAid(aid);
			newAgent.setState(AgentState.IN_TRANSIT);
			newAgent.setType(typeManager.find(agent.getType().getName()));
			
			agentRepo.create(newAgent);
			
			// invoke _onArrival
			activator.onArrival(aid, agent.getType().getName(), serializedData);
			
			// TODO implement acceptance check
		}
		catch(Exception e)
		{
			log.error("Agent <"+agent.getAid().getName()+"> not received properly.", e);
			throw e;
		}
	}
	
	/**
	 * Changes the location of agent's identifier, removes the {@link AgentEntity} from this platform 
	 * and resends all the unread messages.
	 * Also fires {@link MessageRetrievedEvent}.
	 * 
	 * @param agentName
	 * @param destinationPlatform the platform that the agent has traveled to
	 */
	public void completeDeparture(String agentName, String destinationPlatform)
	{
		// update aid
		aidManager.changeLocation(agentName, destinationPlatform);
		
		// remove agent and retrieve remaining messages
		List<ACLMessage> messages = agentRepo.completeDeparture(agentName);
		messages.forEach(messageRetrievedEvent::fire);
		
		// resend messages
		messages.forEach(m->resendMessage(new AgentIdentifier(agentName), m));
	}
	
	/**
	 * Passivates the agent after the transport has failed.
	 */
	public void cancelDeparture(String agentName)
	{
		agentRepo.forcePassivate(agentName);
	}
	
	/**
	 * Passivates the agent after departure from the source platform.
	 */
	public void transportComplete(String agentName)
	{
		agentRepo.forcePassivate(agentName);
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
	
	/**
	 * Creates an {@link AgentIdentifier} that represents this platform.
	 * 
	 * @return AgentIdentifier that represents this platform
	 */
	@Produces @LocalPlatformAid
	public AgentIdentifier getPlatformAid()
	{
		AgentIdentifier platformAid = new AgentIdentifier(localPlatformName);
		platformAid.getAddresses().add(localPlatformAddress);
		return platformAid;
	}
}
