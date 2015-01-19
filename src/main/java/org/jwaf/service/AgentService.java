package org.jwaf.service;

import org.jwaf.service.annotations.ServiceQualifier;

@ServiceQualifier
public abstract class AgentService
{
	public abstract Object call(Object... params);
}
