package org.jwaf.task.persistence.entity;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

public class TaskResult
{
	private AgentIdentifier aid;
	private String employer;
	private String taskType;
	private String content;

	public AgentIdentifier getAid()
	{
		return aid;
	}

	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}

	public String getEmployer()
	{
		return employer;
	}

	public void setEmployer(String employer)
	{
		this.employer = employer;
	}

	public String getTaskType()
	{
		return taskType;
	}

	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
}
