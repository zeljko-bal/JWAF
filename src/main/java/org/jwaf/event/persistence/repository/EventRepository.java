package org.jwaf.event.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.management.AidManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.event.persistence.entity.EventEntity;

@Stateless
@LocalBean
public class EventRepository
{
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private AidManager aidManager;
	
	public EventEntity find(String name)
	{
		return em.find(EventEntity.class, name, LockModeType.READ);
	}
	
	public List<EventEntity> findAll()
	{
		return em.createQuery("SELECT e FROM EventEntity e", EventEntity.class).getResultList();
	}
	
	public boolean exists(String name)
	{
		return find(name) != null;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void register(String name, String type)
	{
		EventEntity event = new EventEntity(name, type);
		
		em.persist(event);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unregister(String eventName)
	{
		EventEntity event = em.find(EventEntity.class, eventName, LockModeType.PESSIMISTIC_WRITE);
		
		em.remove(event);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void subscribe(String agentName, String eventName)
	{
		EventEntity event = em.find(EventEntity.class, eventName, LockModeType.PESSIMISTIC_WRITE);
		
		AgentIdentifier aid = aidManager.find(agentName);
		
		event.getRegisteredAgents().add(aid);
		
		em.merge(event);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unsubscribe(String agentName, String eventName)
	{
		EventEntity event = em.find(EventEntity.class, eventName, LockModeType.PESSIMISTIC_WRITE);
		
		event.getRegisteredAgents().removeIf((AgentIdentifier aid) -> aid.getName().equals(agentName));
		
		em.merge(event);
	}
}
