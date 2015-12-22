package org.jwaf.service;

/**
 * An interface that agent services should implement. The beans implementing this interface are discovered 
 * automatically and will be discoverable by agents.
 * 
 * @author zeljko.bal
 */
public interface AgentService
{
	Object call(Object... params);
}
