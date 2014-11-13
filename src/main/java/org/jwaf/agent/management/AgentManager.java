package org.jwaf.agent.management;

import java.net.URL;
import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.jwaf.agent.annotation.LocalPlatformAid;
import org.jwaf.agent.annotation.event.AgentInitializedEvent;
import org.jwaf.agent.persistence.entity.AgentEntity;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.AgentType;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.performative.PlatformPerformative;
import org.jwaf.platform.annotation.resource.LocalPlatformAddress;
import org.jwaf.platform.annotation.resource.LocalPlatformName;

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
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Inject @LocalPlatformAddress
	private URL localPlatformAddress;
	
	@Inject @AgentInitializedEvent
	private Event<AgentIdentifier> agentInitializedEvent;
	
	@Inject @LocalPlatformAid
	private AgentIdentifier localPlatformAid;
	
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
		
		// new aid with name : random-uuid@local-platform-name
		AgentIdentifier aid = new AgentIdentifier(UUID.randomUUID().toString()+"@"+localPlatformName);
		aid.getAddresses().add(localPlatformAddress);
		aid.getUserDefinedParameters().putAll(request.getParams());
		aid = aidManager.createAid(aid);
		
		AgentEntity newAgent = new AgentEntity(type, aid);
		
		// persist agent
		agentRepo.create(newAgent);
		
		// invoke custom setup method
		activator.setup(aid, request.getType());
		
		// set state to passive
		agentRepo.passivate(aid, true);
		
		agentInitializedEvent.fire(aid);
		
		return aid;
	}
	
	public void requestTermination(String name)
	{
		ACLMessage message = new ACLMessage(PlatformPerformative.SELF_TERMINATE, localPlatformAid);
		message.getReceiverList().add(agentRepo.findView(name).getAid());
		
		messageSender.send(message);
	}
}
