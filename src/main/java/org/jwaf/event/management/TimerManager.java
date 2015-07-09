package org.jwaf.event.management;

import java.io.Serializable;
import java.util.Date;

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

import org.jwaf.event.persistence.entity.TimerEventInfo;
import org.jwaf.event.persistence.entity.TimerEventParam;

@Stateless
@LocalBean
public class TimerManager
{
	@Resource
	private TimerService timerService;
	
	@Inject
	private EventManager eventManager;
	
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
	
	public void unregister(String timerName)
	{
		for(Timer t : timerService.getAllTimers())
		{
			if(t.getInfo() instanceof TimerEventInfo)
			{
				if(((TimerEventInfo)t.getInfo()).getTimerName().equals(timerName))
				{
					t.cancel();
					break;
				}
			}
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
				
				eventManager.fire(info.getEventName(), param);
			}
		}
		catch(NoSuchObjectLocalException ex)
		{
			// TODO log warning??
			System.out.println("warning NoSuchObjectLocalException in timer timeout, timer was canceled");
		}
	}
}
