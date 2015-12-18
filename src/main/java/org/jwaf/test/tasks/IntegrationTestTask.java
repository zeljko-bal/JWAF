package org.jwaf.test.tasks;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.agent.management.AgentManager;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.message.management.MessageSender;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.platform.annotations.resource.LocalPlatformName;
import org.jwaf.task.deployer.TaskDeployer;
import org.jwaf.task.persistence.entity.TaskRequest;

@Stateless
@LocalBean
public class IntegrationTestTask implements TaskDeployer
{
	@Inject
	private AgentManager agentManager;
	
	@Inject
	MessageSender messageSender;
	
	@Inject @LocalPlatformName
	private String localPlatformName;
	
	@Override
	public void deploy(TaskRequest request)
	{
		CreateAgentRequest testCreateReq = new CreateAgentRequest("IntegrationTestAgent");
		AgentIdentifier testAid = agentManager.initialize(testCreateReq);
		
		ACLMessage testMessage = new ACLMessage("test", new AgentIdentifier(localPlatformName))
			.addReceivers(testAid)
			.setContentAsObject(request);
		
		messageSender.send(testMessage);
	}
}
