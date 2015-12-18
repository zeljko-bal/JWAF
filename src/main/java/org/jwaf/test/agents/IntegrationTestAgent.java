package org.jwaf.test.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.entity.CreateAgentRequest;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.base.tools.Persistent;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.test.TestData;
import org.jwaf.test.TestPerformatives;

@Stateless
@LocalBean
public class IntegrationTestAgent extends AbstractTestAgent
{
	private static final String TEST_DATA = "INTEGRATION_TEST_DATA";
	
	@Persistent
	private List<String> agentNames;
	
	@Persistent(type=ArrayList.class)
	private List<TestData> results;
	
	@MessageHandler
	public void initialState(ACLMessage message)
	{
		log.info("activated, running initial tests..");
		
		if(message.getContentAsObject() instanceof TaskRequest)
		{
			TaskRequest taskRequest = (TaskRequest) message.getContentAsObject();
			data.map(TEST_DATA).put("task_employer", taskRequest.getEmployer());
		}
		
		List<AgentIdentifier> agents = initializeTestAgents();
		agentNames = agents.stream()
				.map(a->a.getName())
				.collect(Collectors.toList());
		
		behaviours.changeTo("expect_results");
		
		agents.forEach(this::sendTestMessage);
	}
	
	private void sendTestMessage(AgentIdentifier aid)
	{
		messages.send(new ACLMessage()
				.setPerformative("test")
				.addReceivers(aid));
	}
	
	private List<AgentIdentifier> initializeTestAgents()
	{
		List<Class<?>> testTypes = Arrays.asList(SelfDataTestAgent.class, 
												 AgentCreationTestAgent.class, 
												 EventTestAgent.class, 
												 MessageTestAgent.class, 
												 ServiceTestAgent.class, 
												 TypeTestAgent.class
												 );
		
		return testTypes.stream().map(t->
		{
			String name = t.getSimpleName();
			AgentIdentifier aid = agentDirectory.createAgent(new CreateAgentRequest(name));
			return aid;
		})
		.collect(Collectors.toList());
	}
	
	@MessageHandler(behaviour="expect_results", performative=TestPerformatives.TEST_RESULTS)
	public void onResult(ACLMessage message)
	{
		results.add((TestData) message.getContentAsObject());
		int errorCount = 0;
		
		if(results.size() >= agentNames.size())
		{
			for(TestData result : results)
			{
				log.info(result.getTestName()+":");
				if(result.getErrors().size() == 0)
				{
					log.info("[v] Passed");
				}
				else
				{
					for(String error : result.getErrors())
					{
						log.info("[x] "+error);
						errorCount++;
					}
				}
			}
			
			if(errorCount == 0)
			{
				log.info("All tests PASSED **************************");
			}
			else
			{
				log.info("Some tests FAILED error count = {} XXXXXXXX", errorCount);
			}
			
			self.terminate();
		}
	}
}
