package org.jwaf.agent.services;

import java.util.List;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.task.manager.TaskManager;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

public class TaskServices
{
	private TaskManager taskManager;
	
	public TaskServices(TaskManager taskManager)
	{
		this.taskManager = taskManager;
	}

	private AgentIdentifier aid;
	
	public void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
	}
	
	public void deploy(TaskRequest request)
	{
		request.setEmployer(aid.getName());
		taskManager.deploy(request);
	}
	
	public void submitResult(TaskResult result)
	{
		result.setEmployee(aid.getName());
		taskManager.processResult(result);
	}

	public List<TaskResult> retrieveResultSet(String employer)
	{
		return taskManager.retrieveResultSet(employer);
	}
}
