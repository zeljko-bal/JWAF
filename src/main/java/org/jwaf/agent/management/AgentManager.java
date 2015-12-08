package org.jwaf.agent.management;

import java.net.URL;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
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
import org.jwaf.common.util.AgentNameUtils;
import org.jwaf.message.annotations.events.MessageRetrievedEvent;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.performative.PlatformPerformative;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotations.resource.LocalPlatformAddress;
import org.slf4j.Logger;

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
	private AidManager aidManager;
	
	@Inject
	private AgentTypeManager typeManager;
	
	@Inject
	private AgentActivator activator;
	
	@Inject
	private MessageSender messageSender;
	
	@Inject
	private AgentNameUtils nameUtils;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
	@Inject @LocalPlatformAid
	private AgentIdentifier localPlatformAid;
	
	@Inject @AgentCreatedEvent
	private Event<AgentIdentifier> agentCreatedEvent;
	
	@Inject @AgentRemovedEvent
	private Event<AgentIdentifier> agentRemovedEvent;
	
	@Inject @MessageRetrievedEvent
	private Event<ACLMessage> messageRetrievedEvent;
	
	@Inject
	private Logger log;
	
	public AgentIdentifier initialize(CreateAgentRequest request)
	{
		AgentIdentifier aid = createAgentEntity(request);
		
		// invoke custom setup method
		activator.setup(aid, request.getType());
		
		agentCreatedEvent.fire(aid);
		
		return aid;
	}
	
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
	
	public void requestTermination(String name)
	{
		ACLMessage message = new ACLMessage(PlatformPerformative.SELF_TERMINATE, localPlatformAid);
		message.getReceiverList().add(agentRepo.findView(name).getAid());
		
		messageSender.send(message);
	}
	
	/*
	 * methods delegated to AgentRepository
	 */
	
	public AgentEntityView findView(String name)
	{
		return agentRepo.findView(name);
	}
	
	public boolean hasNewMessages(String name)
	{
		return agentRepo.hasNewMessages(name);
	}

	public List<ACLMessage> retrieveMessages(String name)
	{
		List<ACLMessage> messages = agentRepo.retrieveMessages(name);
		
		// notify that messages have ben retrieved
		messages.forEach(messageRetrievedEvent::fire);
		
		return messages;
	}
	
	public void ignoreNewMessages(String name)
	{
		agentRepo.ignoreNewMessages(name);
	}

	public boolean contains(AgentIdentifier aid)
	{
		return agentRepo.containsAgent(aid);
	}

	public boolean contains(String name)
	{
		return agentRepo.containsAgent(name);
	}
	
	public void remove(String name)
	{
		AgentIdentifier removedAid = agentRepo.findView(name).getAid();
		
		agentRepo.remove(name);
		
		agentRemovedEvent.fire(removedAid);
	}
	
	/*
	 * agent transport methods
	 */

	public AgentEntity depart(String agentName)
	{
		return agentRepo.depart(agentName);
	}

	public void receiveAgent(AgentEntity agent, String serializedData) throws Exception
	{
		try
		{
			AgentIdentifier remoteAid = agent.getAid();
			AgentIdentifier aid = new AgentIdentifier(remoteAid.getName());
			aid.getAddresses().add(localPlatformAddress);
			aid.getResolvers().addAll(remoteAid.getResolvers());
			aid = aidManager.save(aid);
			
			AgentEntity newAgent = new AgentEntity();
			newAgent.setAid(aid);
			newAgent.setState(AgentState.IN_TRANSIT);
			newAgent.setType(typeManager.find(agent.getType().getName()));
			
			agentRepo.create(newAgent);
			
			activator.onArrival(aid, agent.getType().getName(), serializedData);
			
			// TODO implement acceptance check
		}
		catch(Exception e)
		{
			log.error("Agent <"+agent.getAid().getName()+"> not received properly.", e);
			throw e;
		}
	}
	
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

	public void cancelDeparture(String agentName)
	{
		agentRepo.passivate(agentName, true);
	}
	
	public void transportComplete(String agentName)
	{
		agentRepo.passivate(agentName, true);
	}
	
	private void resendMessage(AgentIdentifier aid, ACLMessage message)
	{
		aid.getAddresses().clear();
		message.getReceiverList().clear();
		message.getReceiverList().add(aid);
		
		messageSender.send(message);
	}
}
