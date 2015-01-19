package org.jwaf.agent.services;

import java.util.Date;

import javax.ejb.ScheduleExpression;

import org.jwaf.event.management.TimerManager;

public class TimerServices
{
	private TimerManager timerManager;
	
	public TimerServices(TimerManager timerManager)
	{
		this.timerManager = timerManager;
	}

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
