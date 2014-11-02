package org.jwaf.platform.interfaces.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jwaf.agent.persistence.repository.AidRepository;

@Path("platform")
public class PlatformResource
{
	@Inject
	private AidRepository aidRepo;
	
	@GET
	@Path("aid")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getPlatformAid()
	{
		return Response.ok(aidRepo.getPlatformAid()).build();
	}
}
