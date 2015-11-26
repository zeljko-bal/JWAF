package org.jwaf.test.services;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.common.annotations.attributes.TypeAttribute;
import org.jwaf.service.AgentService;

@Stateless
@LocalBean
@TypeAttribute(key="test_service_attr_key_1",value="test_service_attr_val_1")
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
