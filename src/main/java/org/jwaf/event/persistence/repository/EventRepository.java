package org.jwaf.event.persistence.repository;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.annotation.event.AidReferenceDroppedEvent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AidRepository;
import org.jwaf.event.persistence.entity.EventEntity;

@Stateless
@LocalBean
public class EventRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private AidRepository aidRepo;
	
	@Inject @AidReferenceDroppedEvent
	private Event<String> aidReferenceDroppedEvent;
	
	public EventEntity find(String name)
	{
		return em.find(EventEntity.class, name);
	}
	
	public boolean exists(String name)
	{
		return find(name) != null;
	}
	
	public void register(String name)
	{
		register(name, null);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void register(String name, String type)
	{
		EventEntity event = new EventEntity(name, type);
		
		em.persist(event);
	}
	
	public void unregister(String name)
	{
		EventEntity event = find(name);
		
		unregisterTransactional(event);
		
		event.getRegisteredAgents().forEach((AgentIdentifier aid) -> aidReferenceDroppedEvent.fire(aid.getName()));
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unregisterTransactional(EventEntity event)
	{		
		em.remove(event);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void subscribe(String agentName, String eventName)
	{
		EventEntity event = find(eventName);
		
		AgentIdentifier aid = aidRepo.manageAID(new AgentIdentifier(agentName));
		
		event.getRegisteredAgents().add(aid);
		
		em.merge(event);
	}
	
	public void unsubscribe(String agentName, String eventName)
	{
		unsubscribeTransactional(agentName, eventName);
		
		aidReferenceDroppedEvent.fire(agentName);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void unsubscribeTransactional(String agentName, String eventName)
	{
		EventEntity event = find(eventName);
		
		event.getRegisteredAgents().removeIf((AgentIdentifier aid) -> aid.getName().equals(agentName));
		
		em.merge(event);
	}
}
