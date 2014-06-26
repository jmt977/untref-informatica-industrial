package ar.edu.untref.ingcomputacion.industrial.modelo;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class Configuracion extends Entidad implements Serializable {

	private static final long serialVersionUID = -7369470559710112078L;
	
	private double temperaturaMinima;
	private double temperaturaMaxima;
	private double humedadMinima;
	private double humedadMaxima;
	
	public Configuracion(){};
	
	public Configuracion(double temperaturaMinima, double temperaturaMaxima,
			double humedadMinima, double humedadMaxima) {
		
		this.temperaturaMinima = temperaturaMinima;
		this.temperaturaMaxima = temperaturaMaxima;
		this.humedadMinima = humedadMinima;
		this.humedadMaxima = humedadMaxima;
	}

	public double getTemperaturaMinima() {
		return temperaturaMinima;
	}
	
	public void setTemperaturaMinima(double temperaturaMinima) {
		this.temperaturaMinima = temperaturaMinima;
	}
	
	public double getTemperaturaMaxima() {
		return temperaturaMaxima;
	}
	
	public void setTemperaturaMaxima(double temperaturaMaxima) {
		this.temperaturaMaxima = temperaturaMaxima;
	}
	
	public double getHumedadMinima() {
		return humedadMinima;
	}
	
	public void setHumedadMinima(double humedadMinima) {
		this.humedadMinima = humedadMinima;
	}
	
	public double getHumedadMaxima() {
		return humedadMaxima;
	}
	
	public void setHumedadMaxima(double humedadMaxima) {
		this.humedadMaxima = humedadMaxima;
	}
	
	public void actualizar(Configuracion nuevaConfiguracion) {
		
		this.temperaturaMinima = nuevaConfiguracion.getTemperaturaMinima();
		this.temperaturaMaxima = nuevaConfiguracion.getTemperaturaMaxima();
		this.humedadMinima = nuevaConfiguracion.getHumedadMinima();
		this.humedadMaxima = nuevaConfiguracion.getHumedadMaxima();
	}
	
}