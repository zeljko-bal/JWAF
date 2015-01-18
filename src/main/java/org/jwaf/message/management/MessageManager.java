package org.jwaf.message.management;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

import org.jwaf.agent.management.AgentActivator;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.annotation.event.MessageRetrievedEvent;
import org.jwaf.message.annotation.event.MessageSentEvent;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.message.persistence.entity.TransportMessage;
import org.jwaf.message.persistence.repository.MessageRepository;
import org.jwaf.platform.annotation.resource.LocalPlatformName;
import org.jwaf.platform.message.MessageParam;
import org.jwaf.platform.message.PlatformMessageManager;
import org.jwaf.remote.management.RemotePlatformManager;

/**
 * Session Bean implementation class MessageManager
 */
@Stateless
@LocalBean
public class MessageManager 
{
	@Inject
	private MessageRepository messageRepo;
	
	@Inject
	private AgentManager agentManager;

	@Inject
	private RemotePlatformManager remoteManager;
	
	@Inject
	private AgentActivator activator;
	
	@Inject
	private PlatformMessageManager platformMsgManager;
	
	@Inject @MessageRetrievedEvent
	private Event<ACLMessage> messageRetrievedEvent;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	public void handleMessage(@Observes @MessageSentEvent ACLMessage message)
	{
		handleTransportMessage(new TransportMessage(message, message.getReceiverList()));
	}
	
	public void handleTransportMessage(TransportMessage transportMessage)
	{
		// if already stamped by this platform discard
		if(transportMessage.getStamps().contains(localPlatformName))
		{
			return;
		}
		
		// if this is a platform message pass it to PlatformMessageManager
		if(transportMessage.getContent().getUserDefinedParameters().containsKey(MessageParam.PLATFORM_MESSAGE))
		{
			platformMsgManager.handle(transportMessage.getContent());
			return;
		}
		
		List<AgentIdentifier> local = new ArrayList<>();
		List<AgentIdentifier> remote = new ArrayList<>();
		List<AgentIdentifier> outbox = new ArrayList<>();

		List<AgentIdentifier> recievers = transportMessage.getIntendedReceivers();

		// classify receivers
		recievers.forEach((AgentIdentifier aid)->
		{
			if(agentManager.contains(aid))
			{
				// if agent is local
				local.add(aid);
			}
			else if(!aid.getAddresses().isEmpty())
			{
				// else if agent has addresses
				remote.add(aid);
			}
			else if(remoteManager.containsAid(aid.getName()))
			{
				// else if remoteManager has addresses for this agent
				aid.getAddresses().addAll(remoteManager.findAid(aid.getName()).getAddresses());
				remote.add(aid);
			}
			else
			{
				// else leave in outbox
				outbox.add(aid);
			}
		});
		
		ACLMessage message = transportMessage.getContent();

		// persist if needed
		if(!local.isEmpty() || !outbox.isEmpty())
		{
			// set unread count
			message.setUnreadCount(local.size() + outbox.size());

			messageRepo.persist(message);
		}

		// if agent is local send to local agent
		local.forEach((AgentIdentifier aid)-> sendToLocal(aid, message));
		// if we can locate agent on a remote platform send to remote platform
		remote.forEach((AgentIdentifier aid)-> sendToRemote(aid, transportMessage));
		// if the receiver cannot be located leave message in outbox for manual retrieval
		outbox.forEach((AgentIdentifier aid)-> sendToOutbox(aid, message));
	}

	private void sendToLocal(AgentIdentifier aid, ACLMessage message)
	{
		// link messages to agents and notify them
		activator.activate(aid, message);
	}

	private void sendToRemote(AgentIdentifier aid, TransportMessage transportMessage)
	{
		// obtain the address
		URL address = aid.getAddresses().get(0);

		// stamp the message
		transportMessage.getStamps().add(localPlatformName);

		// set reciever
		transportMessage.getIntendedReceivers().clear();
		transportMessage.getIntendedReceivers().add(aid);
		
		// set date
		if(transportMessage.getDateSent() == null)
		{
			transportMessage.setDateSent(new Date());
		}

		// call remote service
		Client client = ClientBuilder.newClient();
		client.target(address.toString()).path("message").path("transport").request().post(Entity.xml(transportMessage));
	}

	private void sendToOutbox(AgentIdentifier aid, ACLMessage message)
	{
		messageRepo.createOutboxEntry(aid.getName(), message);
	}
	
	public ACLMessage retrieveOutboxMessage(String receiverName)
	{
		ACLMessage message = messageRepo.retrieveOutboxMessage(receiverName);
		
		messageRetrievedEvent.fire(message);
		
		return message;
	}
}
