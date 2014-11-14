package org.jwaf.task.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskResult
{
	@Id @GeneratedValue
	@XmlTransient
	private Integer id;
	
	@ManyToOne(cascade={CascadeType.REFRESH, CascadeType.MERGE})
	@XmlElement
	private AgentIdentifier employee;
	
	@XmlElement
	private String employer;
	
	@XmlElement
	private String taskType;
	
	@Lob 
	@XmlElement
	private String content;

	public AgentIdentifier getEmployee()
	{
		return employee;
	}

	public void setEmployee(AgentIdentifier aid)
	{
		this.employee = aid;
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
