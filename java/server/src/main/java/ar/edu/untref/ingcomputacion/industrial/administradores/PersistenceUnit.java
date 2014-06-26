package ar.edu.untref.ingcomputacion.industrial.administradores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Proveedor de EntityManager para manejar el acceso a BBDD
 * @author mmaisano
 *
 */
public class PersistenceUnit {
	
	private static PersistenceUnit persistenceUnit = new PersistenceUnit();
	private EntityManagerFactory entityManagerFactory;
	
	private PersistenceUnit () {
		this.entityManagerFactory = Persistence.createEntityManagerFactory("compost-PersistenceUnit");
	}
	
	public static PersistenceUnit instancia() {
		return persistenceUnit;
	}
	
	public EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}
}
