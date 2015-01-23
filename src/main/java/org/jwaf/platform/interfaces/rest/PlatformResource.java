package org.jwaf.platform.interfaces.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jwaf.agent.annotations.LocalPlatformAid;
import org.jwaf.agent.persistence.entity.AgentIdentifier;

@Path("platform")
@Stateless
public class PlatformResource
{	
	@Inject @LocalPlatformAid
	private AgentIdentifier localPlatformAid;
	
	@GET
	@Path("aid")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getPlatformAid()
	{
		return Response.ok(localPlatformAid).build();
	}
}
