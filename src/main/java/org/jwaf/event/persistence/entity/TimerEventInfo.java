package org.jwaf.event.persistence.entity;

import java.io.Serializable;

public class TimerEventInfo implements Serializable
{
	private static final long serialVersionUID = 5237748526581590918L;
	
	private String timerName;
	private String eventName;
	
	public TimerEventInfo(String timerName, String eventName)
	{
		this.timerName = timerName;
		this.eventName = eventName;
	}

	public String getTimerName()
	{
		return timerName;
	}

	public void setTimerName(String timerName)
	{
		this.timerName = timerName;
	}

	public String getEventName()
	{
		return eventName;
	}

	public void setEventName(String eventName)
	{
		this.eventName = eventName;
	}
}
