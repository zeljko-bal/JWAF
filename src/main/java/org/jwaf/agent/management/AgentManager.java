package org.jwaf.agent.management;

import java.net.URL;
import java.util.List;
import java.util.Map;

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
import org.jwaf.agent.persistence.repository.AgentDataType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.agent.persistence.repository.DataStore;
import org.jwaf.message.annotations.events.MessageRetrievedEvent;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.performative.PlatformPerformative;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotation.resource.LocalPlatformAddress;
import org.jwaf.util.AgentNameUtils;

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
	private AgentNameUtils agentName;
	
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
	
	public AgentIdentifier initialize(CreateAgentRequest request)
	{
		AgentType type = null;
		
		try
		{
			type = typeManager.find(request.getType());
		}
		catch (NoResultException e)
		{
			e.printStackTrace();
			// TODO throw type doesnt exist
		}
		
		// new aid with name : <random-uuid>:<agent-type>@<local-platform-name>
		AgentIdentifier aid = new AgentIdentifier(agentName.createRandom(type.getName()));
		aid.getAddresses().add(localPlatformAddress);
		aid.getUserDefinedParameters().putAll(request.getParams());
		aid = aidManager.createAid(aid);
		
		AgentEntity newAgent = new AgentEntity(type, aid);
		
		// persist agent
		agentRepo.create(newAgent);
		
		// invoke custom setup method
		activator.setup(aid, request.getType());
		
		agentCreatedEvent.fire(aid);
		
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
	
	public void remove(String name)
	{
		AgentIdentifier removedAid = agentRepo.findView(name).getAid();
		
		agentRepo.remove(name);
		
		agentRemovedEvent.fire(removedAid);
	}

	public List<ACLMessage> getMessages(String name)
	{
		List<ACLMessage> messages = agentRepo.getMessages(name);
		
		// notify that messages have ben retrieved
		messages.forEach((ACLMessage message) -> messageRetrievedEvent.fire(message));
		
		return messages;
	}
	
	public void ignoreNewMessages(String name)
	{
		agentRepo.ignoreNewMessages(name);
	}

	public DataStore getDataStore(String agentName, AgentDataType type)
	{
		return new DataStore(agentRepo, type, agentName);
	}
	
	public Map<String, String> getPublicData(String agentName)
	{
		return agentRepo.getPublicData(agentName);
	}

	public boolean contains(AgentIdentifier aid)
	{
		return agentRepo.containsAgent(aid);
	}

	public boolean contains(String name)
	{
		return agentRepo.containsAgent(name);
	}
	
	/*
	 * agent transport methods
	 */

	public void arrived(String agentName)
	{
		String typeName = findView(agentName).getType().getName();
		activator.onArrival(aidManager.manageAID(new AgentIdentifier(agentName)), typeName);
	}

	public AgentEntity depart(String agentName)
	{
		return agentRepo.depart(agentName);
	}

	public void departed(String agentName)
	{
		agentRepo.remove(agentName);
	}

	public void cancelDeparture(String agentName)
	{
		agentRepo.passivate(aidManager.manageAID(new AgentIdentifier(agentName)), true);
	}

	public boolean receiveAgent(AgentEntity agent)
	{
		AgentIdentifier remoteAid = agent.getAid();
		AgentIdentifier aid = new AgentIdentifier(remoteAid.getName());
		aid.getAddresses().addAll(remoteAid.getAddresses());
		aid.getUserDefinedParameters().putAll(remoteAid.getUserDefinedParameters());
		aid.getResolvers().addAll(remoteAid.getResolvers());
		aid = aidManager.createAid(aid);
		
		AgentEntity newAgent = new AgentEntity();
		newAgent.setAid(aid);
		newAgent.setHasNewMessages(!agent.getMessages().isEmpty());
		newAgent.setState(AgentState.IN_TRANSIT);
		newAgent.setType(typeManager.find(agent.getType().getName()));
		
		agentRepo.create(newAgent);
		
		newAgent.getData(AgentDataType.PUBLIC).putAll(agent.getData(AgentDataType.PUBLIC));
		newAgent.getData(AgentDataType.PRIVATE).putAll(agent.getData(AgentDataType.PRIVATE));
		
		return true; // TODO implement acceptance check
	}
}
