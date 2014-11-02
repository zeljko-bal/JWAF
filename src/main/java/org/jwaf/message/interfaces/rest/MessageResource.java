package org.jwaf.message.interfaces.rest;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.jwaf.message.management.MessageManager;
import org.jwaf.message.persistence.entity.ACLMessage;
import org.jwaf.message.persistence.entity.TransportMessage;
import org.jwaf.util.XMLSchemaUtils;

@Path("message")
public class MessageResource
{
	@Inject
	private MessageManager messageManager;
	
	@POST
	@Path("acl")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response handleMessage(ACLMessage message)
	{
		messageManager.handleMessage(message);
		return Response.accepted().build();
	}

	@POST
	@Path("transport")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response handleTransportMessage(TransportMessage transportMessage)
	{
		messageManager.handleTransportMessage(transportMessage);
		return Response.accepted().build();
	}
	
	@GET
	@Path("outbox/{name}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response retrieveOutboxMessage(@PathParam("name") String receiverName)
	{
		return Response.ok(messageManager.retrieveOutboxMessage(receiverName)).build();
	}

	@GET
	@Path("/acl/schema")
	@Produces(MediaType.APPLICATION_XML)
	public Response getAclMessageSchema() throws IOException, JAXBException
	{
		return Response.ok(XMLSchemaUtils.generate(ACLMessage.class)).build();
	}

	@GET
	@Path("/transport/schema")
	@Produces(MediaType.APPLICATION_XML)
	public Response getTransportMessageSchema() throws IOException, JAXBException
	{
		return Response.ok(XMLSchemaUtils.generate(TransportMessage.class)).build();
	}
}
