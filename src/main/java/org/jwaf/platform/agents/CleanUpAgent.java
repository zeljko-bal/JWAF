package org.jwaf.platform.agents;

import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.repository.AidRepository;
import org.jwaf.agent.template.reactive.AbstractReactiveAgent;
import org.jwaf.agent.template.reactive.annotation.MessageHandler;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class CleanUpAgent extends AbstractReactiveAgent
{
	@Inject
	private AidRepository aidRepo;
	
	public void setup()
	{
		event.register("aid-cleanup-evt"); // TODO check if exists event, timer
		event.subscribe("aid-cleanup-evt");
		timer.register("cleanup-timer", "aid-cleanup-evt", (new ScheduleExpression()).dayOfMonth(15));
	}
	
	@MessageHandler("aid-cleanup")
	public void cleanUp(ACLMessage message)
	{
		System.out.println("CleanUpAgent cleaning uppp");
		aidRepo.cleanUp();
	}
}
