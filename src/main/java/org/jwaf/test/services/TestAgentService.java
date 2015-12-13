package org.jwaf.test.services;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.service.AgentService;

@Stateless
@LocalBean
@TypeAttribute(key="key_1",value="val_1")
public class TestAgentService implements AgentService
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
