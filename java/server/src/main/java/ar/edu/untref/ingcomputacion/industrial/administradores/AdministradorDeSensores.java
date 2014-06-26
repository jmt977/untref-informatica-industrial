package ar.edu.untref.ingcomputacion.industrial.administradores;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.untref.ingcomputacion.industrial.modelo.Muestra;
import ar.edu.untref.ingcomputacion.industrial.modelo.Sensor;

@SuppressWarnings("unchecked")
public class AdministradorDeSensores {

	private Logger log = LoggerFactory.getLogger(AdministradorDeSensores.class);
	
    public Sensor guardar(Sensor sensor) {
    	
    	EntityManager em = PersistenceUnit.instancia().getEntityManager();
    	
    	try {
    
    		em.getTransaction().begin();
    		sensor = em.merge(sensor);
    		em.getTransaction().commit();
    		log.debug("Sensor guardado: " + sensor);
    	
    	} catch (Exception e) {
    		
    		log.error("Error guardando Sensor " + " : " + e);
    		sensor = null;
    	
    	} finally {
    		em.close();
    	}
    	
    	return sensor;
    }

    public Sensor obtener(Long id) {
    	
    	log.info("Buscando Sensor...");

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
    
	public List<Sensor> obtenerTodos() {

		log.info("Buscando Sensores...");

		List<Sensor> sensores = null;
		
		EntityManager em = PersistenceUnit.instancia().getEntityManager();
		
		try {
			
			Query query = em.createQuery("SELECT s FROM Sensor s");
			
			sensores = (List<Sensor>) query.getResultList();
			
		} catch (Exception e) {
    		
    		log.error("Error buscando Sensores: " + e);
    		e.printStackTrace();
    	
    	} finally {
    		em.close();
    	}
		
		return sensores;
	}

	public List<Muestra> obtenerMuestras(Sensor sensor, Integer cantidad) {
		
		if (cantidad == null) {
			cantidad = 1000;
		}
		
		log.info("Buscando " + cantidad + " Muestras para el " + sensor + "...");

		List<Muestra> muestras = null;
		
		EntityManager em = PersistenceUnit.instancia().getEntityManager();
		
		try {
			
			Query query = em.createQuery("SELECT m FROM Muestra m WHERE m.sensor = :sensor")
					.setParameter("sensor", sensor)
					.setMaxResults(cantidad);
			
			muestras = (List<Muestra>) query.getResultList();
			
		} catch (Exception e) {
    		
    		log.error("Error buscando Muestras para el Sensor " + sensor + "..." + e);
    		e.printStackTrace();
    	
    	} finally {
    		em.close();
    	}
		
		return muestras;
	}

	public List<Muestra> obtenerEstadoActual() {
		
		List<Sensor> sensores = obtenerTodos();
		
		List<Muestra> ultimasMuestras = new LinkedList<Muestra>();
		for (Sensor sensor : sensores) {
			
			ultimasMuestras.add( obtenerUltimaMuestra(sensor) );
			
		}
		
		return ultimasMuestras;
	}

	private Muestra obtenerUltimaMuestra(Sensor sensor) {
		
		log.info("Buscando última muestra para el " + sensor + "...");

		Muestra muestra = null;
		
		EntityManager em = PersistenceUnit.instancia().getEntityManager();
		
		try {
			
			Query query = em.createQuery("SELECT m FROM Muestra m where m.sensor = :sensor ORDER BY m.timestamp DESC")
						.setParameter("sensor", sensor)		
						.setMaxResults(1);
			
			muestra = (Muestra) query.getSingleResult();
			
		} catch (Exception e) {
    		
    		log.error("Error buscando la última Muestra para el " + sensor + " ..." + e);
    		e.printStackTrace();
    		muestra = new Muestra(sensor);
    	
    	} finally {
    		em.close();
    	}
		
		return muestra;
	}

}