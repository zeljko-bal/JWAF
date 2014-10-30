package org.jwaf.agent.management;

import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jwaf.agent.AbstractAgent;
import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.agent.persistence.repository.AgentRepository;
import org.jwaf.platform.annotations.AgentJNDIPrefix;

@Stateless
@LocalBean
public class AgentExecutor
{
	@Inject @AgentJNDIPrefix
	private String agentJNDIPrefix;
	
	@Inject
	private AgentRepository agentRepo;

	@Asynchronous
	public void execute(AgentIdentifier aid, String type)
	{
		System.out.println("executing agent activation of agent: "+aid.getName() + ", of type: " + type);

		try
		{
			// find agent by type
			AbstractAgent agentBean = (AbstractAgent)(new InitialContext()).lookup(agentJNDIPrefix + type);

			// set agents identity
			agentBean.setAid(aid);

			boolean done = false;

			// while execution is not done (agent not passivated)
			while(!done)
			{
				// execute
				agentBean.execute();

				// try to passivate
				done = agentRepo.passivate(aid, false);
			}
		}
		catch (NamingException e) 
		{
			// TODO agent not found
			// agent not found
			e.printStackTrace();
			// force agent to passivate
			agentRepo.passivate(aid, true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// force agent to passivate
			agentRepo.passivate(aid, true);
			throw e;
		}
	}
	
	@Asynchronous
	public void setup(AgentIdentifier aid, String type)
	{
		try
		{
			// find agent by type
			AbstractAgent agentBean = (AbstractAgent)(new InitialContext()).lookup(agentJNDIPrefix + type);

			// set agents identity
			agentBean.setAid(aid);
			
			// invoke initial setup
			agentBean.setup();
		}
		catch (NamingException e) 
		{
			// TODO agent not found
			// agent not found
			e.printStackTrace();
		}
		finally
		{
			// force agent to passivate
			agentRepo.passivate(aid, true);
		}
	}
}
