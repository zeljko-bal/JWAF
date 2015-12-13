package org.jwaf.base.implementations.behaviour;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jwaf.base.BaseAgent;
import org.jwaf.base.implementations.behaviour.annotations.InitialBehaviour;
import org.jwaf.base.implementations.behaviour.annotations.MessageHandler;
import org.jwaf.base.implementations.common.MessageCallbackUtil;
import org.jwaf.base.tools.AgentLogger;
import org.jwaf.base.tools.DataTools;
import org.jwaf.base.tools.MessageTools;
import org.jwaf.common.data.map.DataMap;
import org.jwaf.message.persistence.entity.ACLMessage;

public class BehaviourTools
{
	public static final String BEHAVIOUR_DATA = "BEHAVIOUR_DATA";
	public static final String CURRENT_BEHAVIOUR = "CURRENT_BEHAVIOUR";
	
	private Map<String, AgentBehaviour> behaviours;
	private DataTools dataTools;
	private MessageTools messageTools;
	private String initialBehaviour;
	private BaseAgent owner;
	private AgentLogger log;
	
	public BehaviourTools(BaseAgent owner, DataTools dataTools, MessageTools messageTools, AgentLogger log, boolean scanMethods)
	{
		behaviours = new HashMap<String, AgentBehaviour>();
		this.owner = owner;
		this.dataTools = dataTools;
		this.messageTools = messageTools;
		this.log = log;
		if(scanMethods) scanMessageOwnerMethods();
	}
	
	public void handleAll() throws Exception
	{
		List<ACLMessage> messages;
		
		// if defaultMessageHandler is specified
		if(getCurrentBehaviour().hasDefaultHandler())
		{
			messages = messageTools.getAll();
		}
		else
		{
			Set<String> supportedPerformatives = getSupportedPerformatives();
			messages = messageTools.find(q->q.field("performative").in(supportedPerformatives));
		}
		
		handle(messages);
	}
	
	public void handle(ACLMessage message) throws Exception
	{
		behaviours.get(getCurrentBehaviourName()).handle(message);
	}
	
	public void handle(List<ACLMessage> messages) throws Exception
	{
		behaviours.get(getCurrentBehaviourName()).handle(messages);
	}
	
	public Set<String> getSupportedPerformatives()
	{
		return getSupportedPerformatives(getCurrentBehaviourName());
	}
	
	public Set<String> getSupportedPerformatives(String behaviour)
	{
		return behaviours.get(behaviour).getHandlers().keySet();
	}
	
	/*
	 * Method scanning
	 */
	
	public void scanMessageOwnerMethods()
	{
		if(owner.getClass().isAnnotationPresent(InitialBehaviour.class))
		{
			initialBehaviour = owner.getClass().getAnnotation(InitialBehaviour.class).value();
		}
		else
		{
			initialBehaviour = ""; // default initialBehaviour is "" 
		}
		
		// for all owners methods
		for(Method method : owner.getClass().getMethods())
		{
			if(isMessageCallback(method))
			{
				// map method to annotated performative and behaviour
				MessageHandler annotation = method.getAnnotation(MessageHandler.class);
				String performative = annotation.performative();
				String behaviourName = annotation.behaviour();
				
				AgentBehaviour behaviour = behaviours.get(behaviourName);
				
				// if no behaviour exists under this behaviourName create a new one
				if(behaviour == null)
				{
					behaviour = new AgentBehaviour();
					
					if(behaviourName.equals(initialBehaviour))
					{
						initialBehaviour(behaviourName, behaviour);
					}
					else
					{
						behaviour(behaviourName, behaviour);
					}
				}
				
				// add a handler to this behaviour
				if("".equals(performative))
				{
					behaviour.defaultHandler(m -> method.invoke(owner, m));
				}
				else
				{
					behaviour.handler(performative, m -> method.invoke(owner, m));
				}
			}
		}
	}
	
	private boolean isMessageCallback(Method method)
	{
		// has to be annotated with @MessageHandler
		return method.isAnnotationPresent(MessageHandler.class) 
				&& MessageCallbackUtil.isValidMessageCallback(method);
	}
	
	/*
	 * Behaviours
	 */
	
	public BehaviourTools behaviour(String name, AgentBehaviour behaviour)
	{
		behaviours.put(name, behaviour);
		return this;
	}
	
	public BehaviourTools initialBehaviour(String name, AgentBehaviour newBehaviour)
	{
		initialBehaviour = name;
		return behaviour(name, newBehaviour);
	}
	
	public Map<String, AgentBehaviour> getBehaviours()
	{
		return behaviours;
	}
	
	public String getInitialBehaviour()
	{
		return initialBehaviour;
	}
	
	public void changeTo(String newBehaviour)
	{
		if(!behaviours.keySet().contains(newBehaviour))
		{
			log.error("Tried to change to an undefined behaviour: <{}>.", newBehaviour);
			return;
		}
		
		dataTools.map(BEHAVIOUR_DATA).put(CURRENT_BEHAVIOUR, newBehaviour);
	}
	
	public AgentBehaviour getCurrentBehaviour()
	{
		return behaviours.get(getCurrentBehaviourName());
	}
	
	public String getCurrentBehaviourName()
	{
		DataMap behaviourData = dataTools.map(BEHAVIOUR_DATA);
		
		if(!behaviourData.exists())
		{
			changeTo(initialBehaviour);
			return initialBehaviour;
		}
		else
		{
			String currentBehaviour = dataTools.map(BEHAVIOUR_DATA).get(CURRENT_BEHAVIOUR);
			
			if(currentBehaviour == null)
			{
				currentBehaviour = initialBehaviour;
				changeTo(currentBehaviour);
			}
			
			return currentBehaviour;
		}
	}
}
