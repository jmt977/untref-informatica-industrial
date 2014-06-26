package ar.edu.untref.ingcomputacion.industrial.servicios;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ar.edu.untref.ingcomputacion.industrial.administradores.AdministradorDeConfiguracion;
import ar.edu.untref.ingcomputacion.industrial.modelo.Configuracion;

@Path("/configuracion")
public class Configuraciones extends ServicioBasico {

	private AdministradorDeConfiguracion administradorDeConfiguracion;

	@GET
	@Path("/")
	@Produces("application/json")
	public Response obtenerConfiguracion() {
		
		Response response = internalServerErrorResponse();

		Configuracion configuracionActual = getAdministradorDeConfiguracion().obtener();

		if (configuracionActual != null) {
			
			response = Response.status(Response.Status.OK)
					.header("Access-Control-Allow-Origin", "*")
					.entity(configuracionActual).build();
		}

		return response;
	}
	
	@POST
	@Path("/")
	@Produces ("application/json")
	public Response cambiarConfiguracion(@FormParam(value = "temperaturaMinima") double temperaturaMinima,
										@FormParam(value = "temperaturaMaxima") double temperaturaMaxima,
										@FormParam(value = "humedadMinima") double humedadMinima,
										@FormParam(value = "humedadMaxima") double humedadMaxima) {
		
		Response response = internalServerErrorResponse();
		
		Configuracion nuevaConfiguracion = new Configuracion(temperaturaMinima, temperaturaMaxima,
																humedadMinima, humedadMaxima);
		
		boolean exito = getAdministradorDeConfiguracion().actualizar(nuevaConfiguracion);
		
		if (exito) {
			response = okResponse();
		}
		
		return response;
	}
	
	private AdministradorDeConfiguracion getAdministradorDeConfiguracion() {

		if (this.administradorDeConfiguracion == null) {
			 this.administradorDeConfiguracion = new AdministradorDeConfiguracion();
		}
		
		return this.administradorDeConfiguracion;
	}
	
}