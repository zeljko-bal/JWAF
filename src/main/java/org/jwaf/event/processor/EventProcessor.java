package org.jwaf.event.processor;

import java.io.Serializable;

/**
 * An interface that should be implemented by event processors. Beans implementing this interface are discovered 
 * automatically and are invoked whenever an event is fired to process the contents of the event.
 * The class implementing the event processor should be named the same as the event type that it processes.
 * 
 * @author zeljko.bal
 */
public interface EventProcessor
{
	Serializable process(Serializable input);
}
