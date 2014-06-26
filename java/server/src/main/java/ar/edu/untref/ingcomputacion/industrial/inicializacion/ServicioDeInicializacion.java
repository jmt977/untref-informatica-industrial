package ar.edu.untref.ingcomputacion.industrial.inicializacion;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.untref.ingcomputacion.industrial.administradores.AdministradorDeConfiguracion;
import ar.edu.untref.ingcomputacion.industrial.arduino.CompostManager;
import ar.edu.untref.ingcomputacion.industrial.modelo.Configuracion;

public class ServicioDeInicializacion implements ServletContextListener {

	private Logger log = LoggerFactory.getLogger(ServicioDeInicializacion.class);

	private static Enumeration portList;
	
	public void contextInitialized(ServletContextEvent arg0) {
		
		log.info("SISTEMA INICIALIZADO");

		AdministradorDeConfiguracion adminConfiguracion = new AdministradorDeConfiguracion();
		Configuracion configuracion = adminConfiguracion.obtener();
		
		double temperaturaParaEncendido = configuracion.getTemperaturaMaxima();
		double temperaturaParaApagadoDelVentilador = configuracion.getTemperaturaMaxima() - 2;
		
		double humedadParaRegado = configuracion.getHumedadMinima();
		double humedadParaFinRegado = configuracion.getHumedadMaxima();
		
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("/dev/ttyUSB0")) {
					
					CompostManager compostManager = new CompostManager(portId, 200000, 5000, 300000, 5000, 400000, 5000,
							temperaturaParaEncendido, temperaturaParaApagadoDelVentilador,
							humedadParaRegado, humedadParaFinRegado, 10.0, 15.0);
					compostManager.start();
					
				}
			}
		}
		
	}

	public void contextDestroyed(ServletContextEvent arg0) {
	}

}