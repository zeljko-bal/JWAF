package org.jwaf.service;

import java.util.Map;

public interface AgentService
{
	public Object call(Object param);
	public Map<String, String> getAttributes();
}
