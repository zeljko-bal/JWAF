package org.jwaf.task.persistence.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskRequest implements Serializable
{	
	private static final long serialVersionUID = 8997071627130039944L;

	@XmlElement
	private	String employer;
	
	@XmlElement
	private String taskType;
	
	@XmlElement
	private String content;
	
	public TaskRequest()
	{}
	
	public TaskRequest(String employer, String taskType, String content)
	{
		this.employer = employer;
		this.taskType = taskType;
		this.content = content;
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
