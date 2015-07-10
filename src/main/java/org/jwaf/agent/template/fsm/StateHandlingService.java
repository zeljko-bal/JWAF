package org.jwaf.agent.template.fsm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jwaf.agent.AbstractAgent;
import org.jwaf.agent.persistence.repository.AgentDataType;
import org.jwaf.agent.services.AgentLogger;
import org.jwaf.agent.services.AgentServices;
import org.jwaf.agent.services.MessageServices;
import org.jwaf.agent.template.common.AgentMessageHandler;
import org.jwaf.agent.template.common.InvocationExceptionWrapper;
import org.jwaf.agent.template.common.MessageCallbackUtil;
import org.jwaf.agent.template.fsm.annotation.StateCallback;
import org.jwaf.message.persistence.entity.ACLMessage;

public class StateHandlingService
{
	private Map<String, AgentMessageHandler> stateHandlers;
	private AbstractAgent owner;
	private MessageServices messageServices;
	private AgentServices agentServices;
	private String initialState;
	private AgentLogger log;
	
	public static final String CURRENT_FSM_STATE = "CURRENT_FSM_STATE";

	public StateHandlingService(AbstractAgent owner, MessageServices messageServices, AgentServices agentServices, AgentLogger log)
	{
		this.owner = owner;
		this.messageServices = messageServices;
		this.agentServices = agentServices;
		this.log = log;

		initializeStateHandlers();
	}

	private void initializeStateHandlers()
	{
		stateHandlers = new HashMap<>();

		// for all owners methods
		for(Method method : owner.getClass().getMethods())
		{
			if(isStateCallback(method))
			{
				StateCallback callback = method.getAnnotation(StateCallback.class);

				// map method to state name
				stateHandlers.put(callback.state(), (ACLMessage m) -> 
				{
					try
					{
						method.invoke(owner, m);
					} 
					catch (Exception e)
					{
						throw new InvocationExceptionWrapper(e);
					}
				});

				if(callback.initial())
				{
					if(initialState != null)
					{
						log.warn("StateHandlingService: Multiple initial states defined.");
					}
					
					initialState = callback.state();
				}
			}
		}
	}

	private boolean isStateCallback(Method method)
	{
		// has to be annotated with @StateCallback
		if(method.isAnnotationPresent(StateCallback.class))
		{
			return MessageCallbackUtil.isValidMessageCallback(method);
		}
		else
		{
			return false;
		}
	}

	public void invokeStateHandlers() throws Exception
	{
		// for each new message
		for(ACLMessage message : messageServices.getAll())
		{
			String currentState = getCurrentState();
			
			if(currentState == null)
			{
				currentState = initialState;
			}
			
			AgentMessageHandler handler = stateHandlers.get(currentState);

			if(handler != null)
			{
				// handle state
				MessageCallbackUtil.handleMessage(handler, message);
			}
			else
			{
				log.error("StateHandlingService: Undefined current state. <{}>.", currentState);
				return;
			}
		}
	}
	
	public void changeState(String newState)
	{
		if(!stateHandlers.keySet().contains(newState))
		{
			log.error("StateHandlingService: Changing to undefined new state. <{}>.", newState);
			return;
		}
		
		agentServices.getData(AgentDataType.PRIVATE).put(CURRENT_FSM_STATE, newState);
	}
	
	public String getCurrentState()
	{	
		return agentServices.getData(AgentDataType.PRIVATE).get(CURRENT_FSM_STATE);
	}
	
	public String getInitialState()
	{
		return initialState;
	}
	
	public Map<String, AgentMessageHandler> getStateHandlers()
	{
		return stateHandlers;
	}
}
