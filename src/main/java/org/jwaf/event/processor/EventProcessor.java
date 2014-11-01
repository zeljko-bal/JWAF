package org.jwaf.event.processor;

import java.io.Serializable;

public interface EventProcessor
{
	String process(String input);
	Serializable process(Serializable input);
}
