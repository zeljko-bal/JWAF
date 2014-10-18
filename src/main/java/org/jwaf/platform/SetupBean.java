package org.jwaf.platform;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jwaf.agent.entity.AgentEntity;
import org.jwaf.agent.entity.AgentIdentifier;
import org.jwaf.agent.entity.AgentType;
import org.jwaf.message.MessageRepository;
import org.jwaf.message.entity.ACLMessage;

@Singleton
@LocalBean
@Startup
public class SetupBean 
{
	@PersistenceContext
	EntityManager em;
	
	@Inject
	MessageRepository messageRepo;

	@PostConstruct
	void setup()
	{
		try
		{
			System.out.println( "\n\nHello World!\n\n" );

			AgentType type = new AgentType("type1");
			em.persist(type);


			AgentIdentifier aid1 = new AgentIdentifier("agent1@platform1");
			em.persist(aid1);

			AgentEntity agent = new AgentEntity(type, aid1);

			ACLMessage message = new ACLMessage();

			messageRepo.persist(message);

			agent.getMessages().add(message);

			em.persist(agent);
			
			
			em.flush();


			AgentIdentifier aid2 = new AgentIdentifier("agent2@platform1");
			em.persist(aid2);

			agent = new AgentEntity(type, aid2);

			message = new ACLMessage();

			message.getReceiverList().add(aid1);
			message.getReceiverList().add(new AgentIdentifier("agent3@platform2"));
			//message.getReceiverList().add(new AgentIdentifier("agent1@platform1"));

			messageRepo.persist(message);

			agent.getMessages().add(message);

			em.persist(agent);

			System.out.println( "\n\nTest end!\n\n" );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
