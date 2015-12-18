package org.jwaf.event.management;

import java.io.Serializable;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.jwaf.event.exceptions.EventNotFound;
import org.jwaf.event.persistence.entity.TimerEventInfo;
import org.jwaf.event.persistence.entity.TimerEventParam;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class TimerManager
{
	@Resource
	private TimerService timerService;
	
	@Inject
	private EventManager eventManager;
	
	@Inject
	private Logger log;
	
	public Timer find(String eventName, String timerName)
	{
		return timerService.getAllTimers()
				.stream()
				.filter(timer->timer.getInfo() instanceof TimerEventInfo)
				.filter(timer->
				{
					TimerEventInfo info = (TimerEventInfo) timer.getInfo();
					return info.getEventName().equals(eventName) && info.getTimerName().equals(timerName);
				})
				.findFirst()
				.get();
	}
	
	public void register(String timerName, String eventName, Date initialExpiration, long intervalDuration)
	{
		TimerConfig tcfg = createTimeConfig(timerName, eventName);
		timerService.createIntervalTimer(initialExpiration, intervalDuration, tcfg);
	}
	
	public void register(String timerName, String eventName, long initialDuration, long intervalDuration)
	{
		TimerConfig tcfg = createTimeConfig(timerName, eventName);
		timerService.createIntervalTimer(initialDuration, intervalDuration, tcfg);
	}
	
	public void register(String timerName, String eventName, Date expiration)
	{
		TimerConfig tcfg = createTimeConfig(timerName, eventName);
		timerService.createSingleActionTimer(expiration, tcfg);
	}
	
	public void register(String timerName, String eventName, long duration)
	{
		TimerConfig tcfg = createTimeConfig(timerName, eventName);
		timerService.createSingleActionTimer(duration, tcfg);
	}
	
	public void register(String timerName, String eventName, ScheduleExpression schedule)
	{
		TimerConfig tcfg = createTimeConfig(timerName, eventName);
		timerService.createCalendarTimer(schedule, tcfg);
	}

	private TimerConfig createTimeConfig(String timerName, String eventName)
	{
		TimerEventInfo info = new TimerEventInfo(timerName, eventName);
		return new TimerConfig(info, true);
	}
	
	public void unregister( String eventName, String timerName)
	{
		try
		{
			find(eventName, timerName).cancel();
		}
		catch(NoSuchElementException e)
		{
			log.warn("Tried to cancel a nonexistent timer.");
		}
	}
	
	public boolean exists(String eventName, String timerName)
	{
		try
		{
			find(eventName, timerName);
			return true;
		}
		catch(NoSuchElementException e)
		{
			return false;
		}
	}
	
	@Timeout
	public void timeout(Timer timer)
	{
		try
		{
			Serializable serializableInfo = timer.getInfo();
			
			if(serializableInfo instanceof TimerEventInfo)
			{
				TimerEventInfo info = (TimerEventInfo)serializableInfo;
				TimerEventParam param = new TimerEventParam(info.getTimerName(), new Date());
				
				try
				{
					eventManager.fire(info.getEventName(), param);
				}
				catch(EventNotFound e)
				{
					log.error("Timer <{}> registered to a nonexistent event <{}>, canceling timer.", 
							info.getTimerName(), info.getEventName());
					timer.cancel();
				}
			}
		}
		catch(NoSuchObjectLocalException ex)
		{
			log.warn("NoSuchObjectLocalException in timer timeout, timer was canceled.");
		}
	}
}
