package org.jwaf.platform;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@LocalBean
@Startup
public class SetupBean 
{
	@PersistenceContext
	EntityManager em;

	@PostConstruct
	void setup()
	{
		System.out.println( "\n\nHello World!\n\n" );
		
		InitialContext ctx;
		try 
		{
			ctx = new InitialContext();
			
			System.out.println(ctx.lookup("java:global/jwaf/MessageRepositoryyy"));
		} 
		catch (NamingException e) 
		{
			e.printStackTrace();
		}
		
		
		
		
		/*em.persist(new AgentEntity());
		em.persist(new AgentEntity());

		System.out.println( "\n\npersistedd!\n\n" );*/
	}
}
