package org.jwaf.task.persistence.entity;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskResult
{
	@Id
	@XmlTransient
	private ObjectId id;
	
	@XmlElement
	private String employee;
	
	@XmlElement
	private String employer;
	
	@XmlElement
	private String taskType;
	
	@XmlElement
	private String content;

	public TaskResult()
	{}
	
	public TaskResult(String taskType, String content)
	{
		this.taskType = taskType;
		this.content = content;
	}

	public TaskResult(String employer, String taskType, String content)
	{
		this.employer = employer;
		this.taskType = taskType;
		this.content = content;
	}
	
	public String getEmployee()
	{
		return employee;
	}

	public void setEmployee(String employee)
	{
		this.employee = employee;
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
