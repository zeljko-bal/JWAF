package org.jwaf.message.management;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.common.data.mongo.QueryFunction;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.message.persistence.repository.MessageRepository;

@Stateless
@LocalBean
public class MessageFinder
{
	@Inject
	private MessageRepository messageRepo;
	
	public List<ACLMessage> find(List<Integer> messageIDs, QueryFunction<ACLMessage> queryFunc)
	{
		return messageRepo.find(messageIDs, queryFunc);
	}
}
