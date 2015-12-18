package org.jwaf.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class TestData implements Serializable
{
	private static final long serialVersionUID = -6073031395465955124L;
	
	public static final String TEST_DATA = "TEST_DATA";
	
	@Id
	private String id;
	private String testName;
	private List<String> errors;
	
	public TestData()
	{
		id=TEST_DATA;
		errors=new ArrayList<>();
	}
	
	public TestData(String testName)
	{
		this();
		this.testName = testName;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getTestName()
	{
		return testName;
	}

	public void setTestName(String testName)
	{
		this.testName = testName;
	}

	public List<String> getErrors()
	{
		return errors;
	}
}