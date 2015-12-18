package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.base.BaseAgent;
import org.jwaf.common.annotations.attributes.TypeAttribute;

@Stateless
@LocalBean
@TypeAttribute(key="key_1",value="val_1")
@TypeAttribute(key="key_2",value="val_2")
public class SuicidalTestAgentHelper extends BaseAgent
{
	@Override
	protected void execute() throws Exception
	{
		self.terminate();
	}
}
