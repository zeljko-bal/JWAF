package org.jwaf.event.persistence.repository;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.event.persistence.entity.EventEntity;

@Stateless
@LocalBean
public class EventRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private AgentRepository agentRepo;
	
	
	public EventEntity find(String name)
	{
		return em.find(EventEntity.class, name);
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
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unregister(String name)
	{
		EventEntity event = find(name);
		
		em.remove(event);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void subscribe(String agentName, String eventName)
	{
		EventEntity event = find(eventName);
		
		AgentIdentifier aid = agentRepo.findAid(agentName);
		
		event.getRegisteredAgents().add(aid);
		
		em.merge(event);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unsubscribe(String agentName, String eventName)
	{
		EventEntity event = find(eventName);
		
		event.getRegisteredAgents().removeIf((AgentIdentifier aid) -> aid.getName().equals(agentName));
		
		em.merge(event);
	}
}
