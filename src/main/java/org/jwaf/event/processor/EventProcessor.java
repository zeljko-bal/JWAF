package org.jwaf.event.processor;

import java.io.Serializable;

public interface EventProcessor
{
	Serializable process(Serializable input);
}
