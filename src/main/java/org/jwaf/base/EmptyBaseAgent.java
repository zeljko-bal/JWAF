package org.jwaf.base;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jwaf.agent.SingleThreadedAgent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.base.tools.AgentLogger;
import org.jwaf.common.annotations.logger.NamedLogger;
import org.slf4j.Logger;

/**
 * An abstract implementation of the {@link SingleThreadedAgent} that adds lifecycle callbacks 
 * and stores agent's identifier before every invocation, so that the implementing agent appears to 
 * have a state, even though the agent is implemented as a stateless bean.
 * 
 * @author zeljko.bal
 */
public abstract class EmptyBaseAgent implements SingleThreadedAgent
{
	@Inject @NamedLogger("AGENT")
	private Logger logger;
	
	protected AgentLogger log;
	
	protected AgentIdentifier aid;
	
	@PostConstruct
	private void _postConstruct()
	{
		log = new AgentLogger(logger);
		
		postConstruct();
	}
	
	/**
	 * invoked after each bean construction
	 */
	protected void postConstruct()
	{/* no-op */}

	private void setAid(AgentIdentifier aid)
	{
		this.aid = aid;
		
		onSetAid(aid);
	}
	
	/**
	 * invoked before every lifecycle callback
	 * @param aid agents identity
	 */
	protected void onSetAid(AgentIdentifier aid)
	{
		log.setAid(aid);
	}
	
	@Override
	public void _execute(AgentIdentifier aid) throws Exception
	{
		setAid(aid);
		
		preExecute();
		execute();
		postExecute();
	}
	
	protected void preExecute()
	{/* no-op */}
	
	protected abstract void execute() throws Exception;
	
	protected void postExecute()
	{/* no-op */}
	
	@Override
	public void _setup(AgentIdentifier aid)
	{
		setAid(aid);
		
		preSetup();
		setup();
		postSetup();
	}
	
	protected void preSetup()
	{/* no-op */}
	
	protected void setup()
	{/* no-op */}
	
	protected void postSetup()
	{/* no-op */}
	
	@Override
	public void _onArrival(AgentIdentifier aid, String data)
	{
		setAid(aid);
		
		preArrival(data);
		onArrival(data);
		postArrival(data);
	}
	
	protected void preArrival(String data)
	{/* no-op */}
	
	protected void onArrival(String data)
	{/* no-op */}
	
	protected void postArrival(String data)
	{/* no-op */}
}
