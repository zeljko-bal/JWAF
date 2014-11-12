package org.jwaf.event.persistence.entity;

import java.io.Serializable;
import java.util.Date;

public class TimerEventParam implements Serializable
{
	private static final long serialVersionUID = 6965027537459479316L;
	
	private String timerName;
	private Date firedAt;
	
	public TimerEventParam(String timerName, Date firedAt)
	{
		this.timerName = timerName;
		this.firedAt = firedAt;
	}

	public String getTimerName()
	{
		return timerName;
	}

	public void setTimerName(String timerName)
	{
		this.timerName = timerName;
	}

	public Date getFiredAt()
	{
		return firedAt;
	}

	public void setFiredAt(Date firedAt)
	{
		this.firedAt = firedAt;
	}
}
