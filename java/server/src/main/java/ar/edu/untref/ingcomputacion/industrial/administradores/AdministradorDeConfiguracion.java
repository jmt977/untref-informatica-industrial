package ar.edu.untref.ingcomputacion.industrial.administradores;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.untref.ingcomputacion.industrial.modelo.Configuracion;

public class AdministradorDeConfiguracion {

	private Logger log = LoggerFactory.getLogger(AdministradorDeConfiguracion.class);
	
    public Configuracion guardar(Configuracion configuracion) {
    	
    	EntityManager em = PersistenceUnit.instancia().getEntityManager();
    	
    	try {
    
    		em.getTransaction().begin();
    		configuracion = em.merge(configuracion);
    		em.getTransaction().commit();
    		log.debug("Configuracion actualizada");
    	
    	} catch (Exception e) {
    		
    		log.error("Error actualizando Configuracion" + e);
    		configuracion = null;
    	
    	} finally {
    		em.close();
    	}
    	
    	return configuracion;
    }

    public Configuracion obtener() {
    	
    	log.info("Buscando Configuración...");

		Configuracion configuracion = null;
		
		EntityManager em = PersistenceUnit.instancia().getEntityManager();
		
		try {
			
			Query query = em.createQuery("SELECT c FROM Configuracion c ");
			
			configuracion = (Configuracion) query.getSingleResult();
			
		} catch (Exception e) {
    		
    		log.error("ERROR: No existe una única Configuración para el sistema." + e);
    	
    	} finally {
    		em.close();
    	}
		
		return configuracion;
    }

	public boolean actualizar(Configuracion nuevaConfiguracion) {
		
		boolean exito = true;
		
		Configuracion actual = this.obtener();
		Configuracion actualizada = null;
		
		if (actual == null) {

			actualizada = this.guardar(nuevaConfiguracion);
		
		} else {
			
			actual.actualizar(nuevaConfiguracion);
			actualizada = this.guardar(actual);
			
		}
		
		if (actualizada == null) {
			exito = false;
		}
		
 		return exito;
	}
    
}