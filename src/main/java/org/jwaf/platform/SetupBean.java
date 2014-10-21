package org.jwaf.platform;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jwaf.agent.entity.AgentEntity;
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.agent.entity.AgentType;
import org.jwaf.message.MessageManager;
import org.jwaf.message.entity.ACLMessage;

@Singleton
@LocalBean
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class SetupBean 
{
	@PersistenceContext
	EntityManager em;
	
	@Inject
	MessageManager messageManager;
	
	@Resource
	UserTransaction utx;

	@PostConstruct
	void setup()
	{
		try
		{
			
			utx.begin();
			
			System.out.println( "\n\nHello World!\n\n" );
			
			AgentType type = new AgentType("type1");
			em.persist(type);
			
			AgentIdentifier aid1 = new AgentIdentifier("agent1@platform1");
			em.persist(aid1);
			
			AgentIdentifier aid2 = new AgentIdentifier("agent2@platform1");
			em.persist(aid2);
			AgentIdentifier aid3 = new AgentIdentifier("agent3@platform1");
			em.persist(aid3);
			
			AgentEntity agent1 = new AgentEntity(type, aid1);
			em.persist(agent1);
			
			AgentEntity agent2 = new AgentEntity(type, aid2);
			em.persist(agent2);
			
			//////////////////////////
			utx.commit();
			utx.begin();
			/////////////////////////
			
			ACLMessage message1 = new ACLMessage();
			message1.getReceiverList().add(aid1);
			message1.getReceiverList().add(aid2);
			message1.getReceiverList().add(new AgentIdentifier(aid3.getName()));
			message1.getReceiverList().add(new AgentIdentifier("agent4@platform2"));
			
			//em.persist(message1);
			messageManager.handleMessage(message1);
			
			utx.commit();
			
			
			/*
			utx.begin();

			AgentType type = new AgentType("type1");
			em.persist(type);


			AgentIdentifier aid1 = new AgentIdentifier("agent1@platform1");
			em.persist(aid1);
			
			utx.commit();
			utx.begin();

			AgentEntity agent = new AgentEntity(type, aid1);
			
			agent.setState(AgentState.PASSIVE);

			em.persist(agent);
			
			utx.commit();
			utx.begin();
			
			ACLMessage message = new ACLMessage();
			
			message.getReceiverList().add(aid1);
			
			messageManager.handleMessage(message);
			
			em.flush();

			AgentIdentifier aid2 = new AgentIdentifier("agent2@platform1");
			em.persist(aid2);
			
			utx.commit();
			utx.begin();

			agent = new AgentEntity(type, aid2);
			
			em.persist(agent);
			
			utx.commit();
			utx.begin();
			
			message = new ACLMessage();
			
			message.getReceiverList().add(aid1);
			message.getReceiverList().add(new AgentIdentifier("agent3@platform2"));
			//message.getReceiverList().add(new AgentIdentifier("agent1@platform1"));

			messageManager.handleMessage(message);
			
			utx.commit();
*/
			System.out.println( "\n\nTest end!\n\n" );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
