package org.jwaf.task.deployer;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.annotations.LocalPlatformAid;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.performative.PlatformPerformative;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

/**
 * A default implementation of {@link TaskDeployer}. Creates a new agent whose type name is equal to the task 
 * type name and sends him a message with TASK_REQUEST performative and the task request contents.
 * When the result is done a request for self termination is sent to the agent.
 * 
 * @author zeljko.bal
 */
@Stateless
@LocalBean
public class DefaultTaskDeployer implements TaskDeployer
{
	@Inject
	private AgentManager agentManager;
	
	@Inject
	private MessageSender messageSender;
	
	@Inject @LocalPlatformAid
	private AgentIdentifier localPlatformAid;
	
	@Override
	public void deploy(TaskRequest request)
	{
		AgentIdentifier employee = agentManager.initialize(new CreateAgentRequest(request.getTaskType()));
		
		ACLMessage message = new ACLMessage(PlatformPerformative.TASK_REQUEST, localPlatformAid);
		message.setContentAsObject(request);
		message.getReceiverList().add(employee);
		
		messageSender.send(message);
	}

	@Override
	public TaskResult processResult(TaskResult result)
	{
		agentManager.requestTermination(result.getEmployee());
		return result;
	}
}
