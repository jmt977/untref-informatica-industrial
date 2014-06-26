package ar.edu.untref.ingcomputacion.industrial.modelo;

import java.beans.Transient;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Entity
public class Muestra extends Entidad implements Serializable {

	private static final long serialVersionUID = -7615114696608791937L;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private Sensor sensor;
	private Date timestamp;
	private double valor;

	public Muestra(){}
	
	public Muestra(Sensor sensor) {
		this.sensor = sensor;
	}
	
	@SuppressWarnings("unused")
	private String fechaFormateada;
	
	@ManyToOne
	@JoinColumn(name="sensor")
	public Sensor getSensor() {
		return sensor;
	}
	
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
	@JsonIgnore
	@Column
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@Column
	public double getValor() {
		return valor;
	}
	
	public void setValor(double valor) {
		this.valor = valor;
	}
	
	@Transient
	public String getFechaFormateada() {
		
		String fechaFormateada = this.timestamp != null ? sdf.format(timestamp) : null;
		
		return fechaFormateada;
	}
	
	public void setFechaFormateada(String fechaFormateada) {
		this.fechaFormateada = fechaFormateada;
	}
}