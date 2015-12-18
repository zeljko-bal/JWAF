package org.jwaf.test;

import java.util.List;
import java.util.function.BooleanSupplier;

import org.jwaf.agent.persistence.entity.AgentIdentifier;
import org.jwaf.base.tools.AgentLogger;
import org.jwaf.base.tools.DataTools;
import org.jwaf.base.tools.MessageTools;
import org.jwaf.message.persistence.entity.ACLMessage;

public class TestTools
{
	private AgentLogger log;
	private DataTools data;
	private MessageTools messages;
	
	public TestTools(AgentLogger log, DataTools data, MessageTools messages)
	{
		this.log = log;
		this.data = data;
		this.messages = messages;
	}
	
	public void setup(String testName)
	{
		log.info("TEST SETUP");
		data.insert(new TestData(testName));
	}
	
	public void assertEqual(Object o1, Object o2, String message)
	{
		message += " ; " + "<"+o1+"> and <"+o2+"> should be equal.";
		
		if(o1 == null && o2 != null)
		{
			assertTrue(false, message);
		}
		
		assertTrue(o1.equals(o2), message);
	}
	
	public void assertNotEqual(Object o1, Object o2, String message)
	{
		message += " ; " + "<"+o1+"> and <"+o2+"> should be equal.";
		
		if(o1 == null && o2 != null)
		{
			assertTrue(false, message);
		}
		
		assertTrue(!o1.equals(o2), message);
	}
	
	public <T extends Exception> void assertThrows(Class<T> exceptionType, String name, Runnable r)
	{
		try
		{
			r.run();
			assertTrue(false, "Invocation of: <"+name+"> should throw an exception: <"+exceptionType.getSimpleName()+">.");
		}
		catch(Exception e)
		{
			if(!exceptionType.isInstance(e))
			{
				throw e;
			}
		}
	}
	
	public void assertFalse(BooleanSupplier exp, int timeout, String message) throws InterruptedException
	{
		assertTrue(()->!exp.getAsBoolean(), timeout, message);
	}
	
	public void assertTrue(BooleanSupplier exp, int timeout, String message) throws InterruptedException
	{
		for(int i=0;i<(timeout/10);i++)
		{
			if(exp.getAsBoolean())
			{
				return;
			}
			else
			{
				Thread.sleep(10);
			}
		}
		
		onTestError(message);
	}
	
	public void assertFalse(boolean exp, String message)
	{
		assertTrue(!exp, message);
	}
	
	public void assertTrue(boolean exp, String message)
	{
		if(!exp) onTestError(message);
	}
	
	public TestData getTestData()
	{
		return data.find(TestData.class, TestData.TEST_DATA);
	}
	
	public void sendResults(AgentIdentifier aid)
	{
		TestData results = getTestData();
		
		messages.send(new ACLMessage()
				.addReceivers(aid)
				.setPerformative(TestPerformatives.TEST_RESULTS)
				.setContentAsObject(results));
	}
	
	private void onTestError(String message)
	{
		log.error("[x] "+message);
		appendError(message);
	}
	
	public void appendError(String message)
	{
		TestData testData = data.find(TestData.class, TestData.TEST_DATA);
		testData.getErrors().add(message);
		data.save(testData);
	}

	public void appendErrors(List<String> errors)
	{
		errors.forEach(this::appendError);
	}
}
