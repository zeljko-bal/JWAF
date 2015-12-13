package org.jwaf.message.persistence.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.jwaf.common.data.mongo.MongoUtils;
import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.common.data.mongo.annotations.MorphiaDatastore;
import org.jwaf.message.annotations.events.MessageRemovedEvent;
import org.jwaf.message.annotations.events.MessageRetrievedEvent;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.message.persistence.entity.OutboxEntry;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.mongodb.WriteResult;

/**
 * Session Bean implementation class MessageRepository
 */
@Stateless
@LocalBean
public class MessageRepository 
{
	@Inject @MorphiaDatastore
	private Datastore ds;
	
	// TODO remove if unused
	@Inject @MessageRemovedEvent
	private Event<ACLMessage> messageRemovedEvent;
	
	public void persist(ACLMessage message)
	{
		ds.save(message);
	}
	
	public void messageRetrievedEventHandler(@Observes @MessageRetrievedEvent ACLMessage message)
	{
		if(removeUnusedMessage(message))
		{
			messageRemovedEvent.fire(message);
		}
	}
	
	private boolean removeUnusedMessage(ACLMessage message)
	{
		// decrement unreadCount
		ds.update(message, ds.createUpdateOperations(ACLMessage.class)
				.dec("unreadCount"));
		
		// delete message if unreadCount is lessThanOrEq 0
		WriteResult res = ds.delete(ds.find(ACLMessage.class, "id", new ObjectId(message.getId()))
				.field("unreadCount").lessThanOrEq(0));
		
		// return true if message was found and deleted
		return res.getN() > 0;
	}
	
	public void createOutboxEntry(String receiverName, ACLMessage message)
	{
		ds.save(new OutboxEntry(receiverName, message));
	}
	
	public List<ACLMessage> retrieveOutboxMessages(String receiverName)
	{
		Query<OutboxEntry> query = ds.find(OutboxEntry.class, "receiverName", receiverName);
		
		return MongoUtils.findAndDeleteAll(query, ds).stream()
				.map(e -> e.getMessage())
				.collect(Collectors.toList());
	}
	
	public List<ACLMessage> find(List<String> messageIDs, QueryFunction<ACLMessage> queryFunc)
	{
		Query<ACLMessage> query = ds.createQuery(ACLMessage.class);
		
		query = queryFunc.apply(query);
		
		query.filter("_id in", messageIDs.stream()
											.map(id->new ObjectId(id))
											.collect(Collectors.toList()));
		
		return query.asList();
	}
}

