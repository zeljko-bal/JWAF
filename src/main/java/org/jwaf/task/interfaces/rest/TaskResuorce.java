package org.jwaf.task.interfaces.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jwaf.task.manager.TaskManager;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

@Path("task")
@Stateless
public class TaskResuorce
{
	@Inject
	private TaskManager taskManager;
	
	@POST
	@Path("request")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response deploy(TaskRequest request)
	{
		taskManager.deploy(request);
		return Response.ok().build();
	}
	
	@GET
	@Path("result/{employer}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getResultSet(@PathParam("employer") String employer)
	{
		GenericEntity<List<TaskResult>> entity = new GenericEntity<List<TaskResult>>(taskManager.retrieveResultSet(employer)) {};
		return Response.ok(entity).build();
	}
}
