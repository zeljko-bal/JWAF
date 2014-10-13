package org.jwaf;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@LocalBean
@Startup
public class App 
{
	@PersistenceContext
	EntityManager em;

	@PostConstruct
	void setup()
	{
		System.out.println( "\n\nHello World!\n\n" );
		
		em.persist(new AgentType());
		em.persist(new AgentType());

		System.out.println( "\n\npersistedd!\n\n" );
	}
}
