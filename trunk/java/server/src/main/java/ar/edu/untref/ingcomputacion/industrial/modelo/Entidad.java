package ar.edu.untref.ingcomputacion.industrial.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Entidad implements Serializable {

	private static final long serialVersionUID = -4245338456374851130L;

	private Long id;

	public Entidad() {

	}
	
	public Entidad(Long id) {

		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	public Long getId() {

		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object other) {

		boolean equals = (this == other);

		if ((!equals) && (other != null)
				&& (this.getClass().isAssignableFrom(other.getClass()))) {

			Entidad castOther = Entidad.class.cast(other);

			equals = (this.id != null) && (castOther.id != null)
					&& this.id.equals(castOther.id);
		}

		return equals;
	}

	@Override
	public int hashCode() {

		return this.id != null ? this.id.hashCode() : super.hashCode();
	}
}
