package org.jwaf.task.deployer;

import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

public interface TaskDeployer
{
	void deploy(TaskRequest request);
	default TaskResult processResult(TaskResult result)
	{
		return result;
	}
}
