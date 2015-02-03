package org.jwaf.test.agents;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jwaf.agent.template.fsm.AbstractFSMAgent;
import org.jwaf.agent.template.fsm.annotation.StateCallback;
import org.jwaf.message.persistence.entity.ACLMessage;

@Stateless
@LocalBean
public class TransportTestAgent extends AbstractFSMAgent
{
	@StateCallback(state="initial_state", initial=true)
	public void initialState(ACLMessage newMessage)
	{
		remotePlatforms.travelTo(localPlatformName);
	}
	
	@Override
	public void onArrival()
	{
		System.out.println("!@!!!!!!!!!!!!!!!!!!!!!arrived!!!!!!!!!!!!!!!!");
	}
}
