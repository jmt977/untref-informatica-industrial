package ar.edu.untref.ingcomputacion.industrial.administradores;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.untref.ingcomputacion.industrial.modelo.Muestra;
import ar.edu.untref.ingcomputacion.industrial.modelo.Sensor;

public class AdministradorDeMuestras {

	private Logger log = LoggerFactory.getLogger(AdministradorDeMuestras.class);
	
    public void guardar(double valor, long idSensor) {
    	
    	Sensor sensor = obtenerSensor(idSensor);
    	
    	Muestra muestra = new Muestra();
    	muestra.setTimestamp(new Date());
    	muestra.setSensor(sensor);
    	muestra.setValor(valor);
    	
    	EntityManager em = PersistenceUnit.instancia().getEntityManager();
    	
    	try {
    
    		em.getTransaction().begin();
    		em.merge(muestra);
    		em.getTransaction().commit();
    		log.debug("Muestra guardada: " + sensor);
    	
    	} catch (Exception e) {
    		
    		log.error("Error guardando Muestra " + " : " + e);
    	
    	} finally {
    		em.close();
    	}
    	
    }
    
    public Sensor obtenerSensor(Long id) {
    	
		Sensor sensor = null;
		
		EntityManager em = PersistenceUnit.instancia().getEntityManager();
		
		try {
			
			Query query = em.createQuery("SELECT s FROM Sensor s where s.id = :id")
							.setParameter("id", id);
			
			sensor = (Sensor) query.getSingleResult();
			
		} catch (Exception e) {
    		
    		log.error("Error buscando Sensor id " + id + ":" + e);
    		e.printStackTrace();
    	
    	} finally {
    		em.close();
    	}
		
		return sensor;
    }

	public void limpiarRegistros() {

		log.info("PROCEDIENDO A LIMPIAR REGISTROS DE MUESTRAS");
		
EntityManager em = PersistenceUnit.instancia().getEntityManager();
		
		try {
			
			em.getTransaction().begin();
			
			Query queryMaxId = em.createQuery("SELECT MAX(id) FROM Muestra");
			Long maximoId = (Long) queryMaxId.getSingleResult();
			
			Query query = em.createQuery("DELETE FROM Muestra m WHERE m.id < :maximoId ")
					.setParameter("maximoId", maximoId - 3000);
			
			int registrosBorrados = query.executeUpdate();
			
			em.getTransaction().commit();
			log.info("SE HAN BORRADO " + registrosBorrados + " REGISTROS");
			
		} catch (Exception e) {
    		
    		log.error("Error limpiando registros de muestras:" + e);
    		e.printStackTrace();
    		em.getTransaction().rollback();
    	
    	} finally {
    		em.close();
    	}
		
	}

	
}
