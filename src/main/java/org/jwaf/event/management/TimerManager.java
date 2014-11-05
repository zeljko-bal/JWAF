package org.jwaf.event.management;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TimerService;
import javax.inject.Inject;

@Stateless
@LocalBean
public class TimerManager
{
	@Resource
	private TimerService timerService;
	
	@Inject
	private EventManager eventManager;
	
	// TODO TimerManager
	
	/*
	public void registerTimer(String timerName, String eventName, TimerOptions)
	{
		timerService.createTimer(initialExpiration, intervalDuration, info)...
	}
	
	public void unregisterTimer(String timerName)
	{
		timerService.createTimer(initialExpiration, intervalDuration, info)...
	}
	
	@Timeout
	public void timeout(Timer timer)
	{
	    eventManager.fire(eventName, content);
	}*/
}
