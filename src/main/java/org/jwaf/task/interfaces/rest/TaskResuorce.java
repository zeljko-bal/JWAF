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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jwaf.task.manager.TaskManager;
import org.jwaf.task.persistence.entity.TaskRequest;
import org.jwaf.task.persistence.entity.TaskResult;

import com.sun.jersey.api.JResponse;

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
	@Produces(MediaType.APPLICATION_XML)
	public JResponse<List<TaskResult>> getResultSet(@PathParam("employer") String employer)
	{
		return JResponse.ok(taskManager.retrieveResultSet(employer)).build();
	}
}
