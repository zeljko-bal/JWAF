package org.jwaf.data.interfaces.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jwaf.data.management.AgentDataManager;

@Path("agent_data")
@Stateless
public class DataResource
{
	@Inject
	private AgentDataManager agentDataManager;
	
	@GET
	@Path("public/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getPublicData(@PathParam("name") String agentName)
	{
		return Response.ok(agentDataManager.getPublicData(agentName).toJson()).build();
	}
}
