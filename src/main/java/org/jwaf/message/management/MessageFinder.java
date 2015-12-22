package org.jwaf.message.management;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.message.persistence.repository.MessageRepository;

/**
 * A helper bean for finding messages based on a query.
 * 
 * @author zeljko.bal
 */
@Stateless
@LocalBean
public class MessageFinder
{
	@Inject
	private MessageRepository messageRepo;
	
	/**
	 * Find messages with one of given messages ids and that satisfy the given query.
	 * 
	 * @param messageIDs each of the returned messages must have an id that is in this list
	 * @param queryFunc a function that configures the query
	 * @return the list of found messages
	 */
	public List<ACLMessage> find(List<String> messageIDs, QueryFunction<ACLMessage> queryFunc)
	{
		return messageRepo.find(messageIDs, queryFunc);
	}
}
