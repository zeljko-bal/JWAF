package org.jwaf.platform.agents;

import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.repository.AidRepository;
import org.jwaf.agent.template.reactive.AbstractReactiveAgent;
import org.jwaf.agent.template.reactive.annotation.HandleMessage;

@Stateless
@LocalBean
public class CleanUpAgent extends AbstractReactiveAgent
{
	@Inject
	private AidRepository aidRepo;
	
	public void setup()
	{
		eventServices.register("aid-cleanup"); // TODO check if exists event, timer
		eventServices.subscribe("aid-cleanup");
		eventServices.registerTimer("cleanup-timer", "aid-cleanup", (new ScheduleExpression()).dayOfMonth(15));
	}
	
	@HandleMessage("aid-cleanup")
	public void cleanUp()
	{
		aidRepo.cleanUp();
	}
}
