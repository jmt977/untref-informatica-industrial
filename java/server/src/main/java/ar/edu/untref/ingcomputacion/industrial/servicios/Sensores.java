package ar.edu.untref.ingcomputacion.industrial.servicios;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ar.edu.untref.ingcomputacion.industrial.administradores.AdministradorDeSensores;
import ar.edu.untref.ingcomputacion.industrial.modelo.Muestra;
import ar.edu.untref.ingcomputacion.industrial.modelo.Sensor;

@Path("/sensores")
public class Sensores extends ServicioBasico {

	private AdministradorDeSensores administradorDeSensores;

	@GET
	@Path("/")
	@Produces("application/json")
	public Response obtenerSensores() {
		
		Response response = internalServerErrorResponse();

		List<Sensor> sensores = getAdministradorDeSensores().obtenerTodos();

		response = Response.status(Response.Status.OK)
					.header("Access-Control-Allow-Origin", "*")
					.entity(sensores).build();

		return response;
	}
	
	@GET
	@Path("/historico")
	@Produces("application/json")
	public Response obtenerHistorico(@QueryParam("id") Long idSensor,
									@QueryParam("cantidad") Integer cantidad) {
		
		Response response = internalServerErrorResponse();

		Sensor sensor = getAdministradorDeSensores().obtener(idSensor);
		
		if (sensor != null && cantidad >= 1 && cantidad <= 1000) {
			
			List<Muestra> muestras = getAdministradorDeSensores().obtenerMuestras(sensor, cantidad);
			
			response = Response.status(Response.Status.OK)
					.header("Access-Control-Allow-Origin", "*")
					.entity(muestras).build();
		} else {
			response = badRequestResponse();
		}

		return response;
	}
	
	@GET
	@Path("/estadoActual")
	@Produces("application/json")
	public Response obtenerEstadoActual() {
		
		Response response = noContentResponse();

		List<Muestra> muestras = getAdministradorDeSensores().obtenerEstadoActual();
			
		response = Response.status(Response.Status.OK)
				.header("Access-Control-Allow-Origin", "*")
				.entity(muestras).build();

		return response;
	}
	
	
	private AdministradorDeSensores getAdministradorDeSensores() {

		if (this.administradorDeSensores == null) {
			 this.administradorDeSensores = new AdministradorDeSensores();
		}
		
		return this.administradorDeSensores;
	}
	
	public void setAdministradorDeusuarios(AdministradorDeSensores administrador) {
		this.administradorDeSensores = administrador;
	}

}