package org.jwaf.base.implementations.fsm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.jwaf.base.BaseAgent;
import org.jwaf.base.implementations.common.AgentMessageHandler;
import org.jwaf.base.implementations.common.InvocationExceptionWrapper;
import org.jwaf.base.implementations.common.MessageCallbackUtil;
import org.jwaf.base.implementations.fsm.annotation.StateCallback;
import org.jwaf.base.tools.AgentLogger;
import org.jwaf.base.tools.DataTools;
import org.jwaf.base.tools.MessageTools;
import org.jwaf.message.persistence.entity.ACLMessage;

import com.mongodb.client.model.UpdateOptions;

public class StateHandlingTools
{
	private Map<String, AgentMessageHandler> stateHandlers;
	private BaseAgent owner;
	private MessageTools messageTools;
	private DataTools dataTools;
	private String initialState;
	private AgentLogger log;
	
	public static final String FSM_DATA = "FSM_DATA";
	public static final String CURRENT_FSM_STATE = "CURRENT_FSM_STATE";
	private static final Document QUERY = new Document("_id", FSM_DATA);

	public StateHandlingTools(BaseAgent owner, MessageTools messageTools, DataTools dataTools, AgentLogger log)
	{
		this.owner = owner;
		this.messageTools = messageTools;
		this.dataTools = dataTools;
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
		for(ACLMessage message : messageTools.getAll())
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
		
		dataTools.getCollection().replaceOne(QUERY, new Document(CURRENT_FSM_STATE, newState), 
				new UpdateOptions().upsert(true));
	}
	
	public String getCurrentState()
	{
		return dataTools.getCollection()
				.find(QUERY).first()
				.getString(CURRENT_FSM_STATE);
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
