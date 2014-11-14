package org.jwaf.task.deployer;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.annotation.LocalPlatformAid;
import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.management.CreateAgentRequest;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.performative.PlatformPerformative;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

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
		agentManager.requestTermination(result.getEmployee().getName());
		return result;
	}
}
