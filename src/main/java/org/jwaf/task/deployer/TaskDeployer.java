package org.jwaf.task.deployer;

import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

/**
 * An interface that should be implemented by task deployers. Task deployers are discovered automatically and 
 * are invoked when a task request is made to deploy the task and when the results are done to process them additionally.
 * The name of the class of the bean that implements this interface should be the same as the type name of the task.
 * If no TaskDeployer is found the {@link DefaultTaskDeployer} will be used.
 * 
 * @author zeljko.bal
 */
public interface TaskDeployer
{
	void deploy(TaskRequest request);
	default TaskResult processResult(TaskResult result)
	{
		return result;
	}
}
