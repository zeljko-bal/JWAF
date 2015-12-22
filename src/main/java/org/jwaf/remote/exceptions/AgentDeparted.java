package org.jwaf.remote.exceptions;

import javax.ejb.ApplicationException;

import org.jwaf.base.tools.RemotePlatformTools;
import org.jwaf.common.exceptions.AgentSuccessException;

/**
 * Thrown by {@link RemotePlatformTools} when the agent starts the transport process to another platform.
 * Do not catch this exception in agent code.
 * 
 * @author zeljko.bal
 */
@ApplicationException
public class AgentDeparted extends AgentSuccessException
{
	private static final long serialVersionUID = 1L;

	public AgentDeparted(String string)
	{
		super(string);
	}
}
