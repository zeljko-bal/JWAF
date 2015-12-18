package org.jwaf.test.agents;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.event.persistence.entity.TimerEventParam;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class EventTestAgent extends AbstractTestAgent
{
	private static final String	TEST_EVENT = "TEST_EVENT";
	private static final String	STRING_CONTENT = "STRING_CONTENT";
	private static final Date DATE_CONTENT = new Date(42);
	private static final String	TEST_TIMER = "TEST_TIMER";
	private static final String	TEST_DATA = "MY_TEST_DATA";
	
	@MessageHandler
	public void test(ACLMessage message)
	{
		data.map(TEST_DATA).put("employer", message.getSender().getName());
		
		events.register(TEST_EVENT);
		
		t.assertTrue(events.exists(TEST_EVENT), "events.exists(TEST_EVENT)");
		
		events.subscribe(TEST_EVENT);
		
		behaviours.changeTo("expecting_string_event");
		
		events.fire(TEST_EVENT, STRING_CONTENT);
	}
	
	@MessageHandler(behaviour="expecting_string_event")
	public void expectingEvent(ACLMessage message)
	{
		t.assertEqual(message.getContent(), STRING_CONTENT, "message.getContent() == STRING_CONTENT");
		t.assertEqual(message.getPerformative(), TEST_EVENT, "message.getPerformative() == TEST_EVENT");
		
		behaviours.changeTo("expecting_date_event");
		
		events.fire(TEST_EVENT, DATE_CONTENT);
	}
	
	@MessageHandler(behaviour="expecting_date_event")
	public void expectingDateEvent(ACLMessage message)
	{
		t.assertEqual(message.getContentAsObject(), DATE_CONTENT, "message.getContent() == DATE_CONTENT");
		t.assertEqual(message.getPerformative(), TEST_EVENT, "message.getPerformative() == TEST_EVENT");
		
		behaviours.changeTo("expecting_timer_event");
		
		timers.register(TEST_TIMER, TEST_EVENT, (new ScheduleExpression()).hour("*").minute("*").second("*/1"));
		
		t.assertTrue(timers.exists(TEST_EVENT, TEST_TIMER), "timers.exists(TEST_EVENT, TEST_TIMER)");
	}
	
	@MessageHandler(behaviour="expecting_timer_event")
	public void expectingTimerEvent(ACLMessage message)
	{
		t.assertEqual(message.getPerformative(), TEST_EVENT, "message.getPerformative() == TEST_EVENT");
		
		TimerEventParam param = (TimerEventParam) message.getContentAsObject();
		t.assertEqual(param.getTimerName(), TEST_TIMER, "param.getTimerName() == TEST_TIMER");
		
		timers.unregister(TEST_EVENT, TEST_TIMER);
		
		t.assertFalse(timers.exists(TEST_EVENT, TEST_TIMER), "timers.exists(TEST_EVENT, TEST_TIMER) after unregister");
		
		events.unregister(TEST_EVENT);
		
		t.assertFalse(events.exists(TEST_EVENT), "events.exists(TEST_EVENT) after unregister");
		
		String employer = data.map(TEST_DATA).get("employer");
		t.sendResults(new AgentIdentifier(employer));
		self.terminate();
	}
}
