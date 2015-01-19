package org.jwaf.agent.test;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.common.annotations.TypeAttribute;
import org.jwaf.common.annotations.TypeAttributes;
import org.jwaf.service.AgentService;

@Stateless
@LocalBean
@TypeAttributes(@TypeAttribute(key="test-service-attr-key-1",value="test-service-attr-val-1"))
public class TestAgentService extends AgentService
{
	@Override
	public Object call(Object... params)
	{
		String ret = "params=";
		for(Object param : params)
		{
			ret+=param;
		}
		return ret;
	}
}
