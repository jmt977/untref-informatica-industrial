package ar.edu.untref.ingcomputacion.industrial.servicios;

import javax.ws.rs.core.Response;

public class ServicioBasico {

	public Response unauthorizedResponse() {
		return Response.status(Response.Status.UNAUTHORIZED)
				.header("Access-Control-Allow-Origin", "*").build();
	}

	public Response badRequestResponse() {
		return Response.status(Response.Status.BAD_REQUEST)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	public Response createdResponse() {
		return Response.status(Response.Status.CREATED)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	public Response noContentResponse() {
		return Response.status(Response.Status.NO_CONTENT)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	public Response internalServerErrorResponse() {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	public Response okResponse() {
		return Response.status(Response.Status.OK)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
}