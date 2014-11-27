package org.jwaf.agent.services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.task.manager.TaskManager;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

@Stateless
@LocalBean
public class TaskServices
{
	@Inject
	private TaskManager taskManager;
	
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