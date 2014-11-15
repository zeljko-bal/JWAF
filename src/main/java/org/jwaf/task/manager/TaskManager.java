package org.jwaf.task.manager;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.platform.annotation.resource.EJBJNDIPrefix;
import org.jwaf.task.deployer.DefaultTaskDeployer;
import org.jwaf.task.deployer.TaskDeployer;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;
import org.jwaf.task.persistence.repository.TaskResultRepository;

@Stateless
@LocalBean
public class TaskManager
{
	@Inject
	private DefaultTaskDeployer defaultDeployer;
	
	@Inject
	private TaskResultRepository resultRepo;
	
	@Inject @EJBJNDIPrefix
	private String ejbJNDIPrefix;
	
	public void deploy(TaskRequest request)
	{
		TaskDeployer deployer = findDeployer(request.getTaskType());
		
		if(deployer != null)
		{
			deployer.deploy(request);
		}
		else
		{
			defaultDeployer.deploy(request);
		}
	}
	
	public void processResult(TaskResult result)
	{
		TaskResult toPersist = null;
		
		TaskDeployer deployer = findDeployer(result.getTaskType());
		
		if(deployer != null)
		{
			toPersist = deployer.processResult(result);
		}
		else
		{
			toPersist = defaultDeployer.processResult(result);
		}
		
		resultRepo.persist(toPersist);
	}

	public List<TaskResult> retrieveResultSet(String employer)
	{
		return resultRepo.retrieveResultSet(employer);
	}
	
	private TaskDeployer findDeployer(String type)
	{
		try
		{
			return (TaskDeployer)(new InitialContext()).lookup(ejbJNDIPrefix + type);
		}
		catch (NamingException | ClassCastException e)
		{
			// TODO log cant find TaskDeployer of type
			return null;
		}
	}
}
