package org.jwaf.platform.agents;

import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.implementations.reactive.AbstractReactiveAgent;
import org.jwaf.agent.implementations.reactive.annotation.MessageHandler;
import org.jwaf.agent.management.AidManager;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class CleanUpAgent extends AbstractReactiveAgent
{
	@Inject
	private AidManager aidManager;
	
	public void setup()
	{
		event.register("aid_cleanup_evt"); // TODO check if exists event, timer
		event.subscribe("aid_cleanup_evt");
		timer.register("cleanup_timer", "aid_cleanup_evt", (new ScheduleExpression()).dayOfMonth(15));
	}
	
	@MessageHandler("aid_cleanup_evt")
	public void cleanUp(ACLMessage newMessage)
	{
		doCleanup();
	}
	
	@MessageHandler("aid_cleanup_request")
	public void cleanUpRequest(ACLMessage newMessage)
	{
		doCleanup();
		message.send(new ACLMessage().setPerformative("cleanup_done").addReceivers(newMessage.getSender()));
	}
	
	private void doCleanup()
	{
		log.info("Cleaning up.");
		aidManager.cleanUp();
	}
}
