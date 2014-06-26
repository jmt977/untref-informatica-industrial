package ar.edu.untref.ingcomputacion.industrial.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Sensor extends Entidad implements Serializable {

	private static final long serialVersionUID = -2493634571227084541L;

	private TipoSensor tipo;
	private String descripcion;
	
	@Enumerated(EnumType.STRING)
	@Column
	public TipoSensor getTipo() {
		return tipo;
	}
	
	public void setTipo(TipoSensor tipo) {
		this.tipo = tipo;
	}
	
	@Column
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "Sensor [tipo=" + tipo + ", descripcion=" + descripcion + "]";
	}
	
}