package org.jwaf.test.agents;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.base.BaseAgent;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.test.TestTools;

@Stateless
@LocalBean
public class MessageQueryTestHelper extends BaseAgent
{
	protected TestTools t;
	
	@Override
	protected void execute() throws Exception
	{
		if(messages.getCount() >= 3)
		{
			// no new messages since this activation
			t.assertFalse(messages.newMessagesAvailable(), "messages.newMessagesAvailable() after last message");
			
			List<ACLMessage> newMessages = messages.find(q->q.field("performative").equal("perf1"));
			
			newMessages.forEach(m->t.assertEqual(m.getPerformative(), "perf1", "query performative"));
			t.assertEqual(messages.getCount(), 1, "messages.getCount() == 1");
			
			messages.ignoreAndForgetNewMessages();
			
			t.assertEqual(messages.getCount(), 0, "messages.getCount() == 0 after forget");
			t.assertEqual(messages.getAll().size(), 0, "messages.getAll().size() == 0 after forget");
			
			t.sendResults(newMessages.get(0).getSender());
			self.terminate();
		}
	}
	
	@Override
	protected void postConstruct() 
	{
		super.postConstruct();
		t = new TestTools(log, data, messages);
	}
	
	@Override
	protected void setup()
	{
		super.setup();
		t.setup(self.getType().getName());
	}
}
