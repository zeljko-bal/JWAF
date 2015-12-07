package org.jwaf.task.persistence.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jwaf.common.data.mongo.MongoUtils;
import org.jwaf.common.data.mongo.annotations.MorphiaDatastore;
import org.jwaf.task.persistence.entity.TaskResult;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

@Stateless
@LocalBean
public class TaskResultRepository
{
	@Inject @MorphiaDatastore
	private Datastore ds;
	
	public void persist(TaskResult result)
	{
		ds.save(result);
	}
	
	public List<TaskResult> retrieveResultSet(String employer)
	{
		Query<TaskResult> query = ds.createQuery(TaskResult.class)
				.field("employer").equal(employer);
		
		return MongoUtils.findAndDeleteAll(query, ds);
	}
}
