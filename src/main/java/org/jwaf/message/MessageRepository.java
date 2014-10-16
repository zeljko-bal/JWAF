package org.jwaf.message;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.message.entity.ACLMessage;
import org.jwaf.message.entity.ACLMessageQuery;

/**
 * Session Bean implementation class MessageRepository
 */
@Stateless
@LocalBean
public class MessageRepository 
{

	@PersistenceContext
	EntityManager em;
    
    public void putMessage(ACLMessage message)
    {
    	message.setUnreadCount(message.getReceiverList().size());
    	em.persist(message);
    }
    
    public List<ACLMessage> getMessages()
    {
    	return getMessages(new ACLMessageQuery());
    }
    
    public List<ACLMessage> getMessages(ACLMessageQuery querry)
    {
    	// TODO
		return null;
    }
}
