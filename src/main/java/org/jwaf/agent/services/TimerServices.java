package org.jwaf.agent.services;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.event.management.TimerManager;

@Stateless
@LocalBean
public class TimerServices
{
	@Inject
	private TimerManager timerManager;
	
	public void register(String timerName, String eventName, Date initialExpiration, long intervalDuration)
	{
		timerManager.register(timerName, eventName, initialExpiration, intervalDuration);
	}
	
	public void register(String timerName, String eventName, long initialDuration, long intervalDuration)
	{
		timerManager.register(timerName, eventName, initialDuration, intervalDuration);
	}
	
	public void register(String timerName, String eventName, Date expiration)
	{
		timerManager.register(timerName, eventName, expiration);
	}
	
	public void register(String timerName, String eventName, ScheduleExpression schedule)
	{
		timerManager.register(timerName, eventName, schedule);
	}
	
	public void register(String timerName, String eventName, long duration)
	{
		timerManager.register(timerName, eventName, duration);
	}
	
	public void unregister(String timerName)
	{
		timerManager.unregister(timerName);
	}
}
