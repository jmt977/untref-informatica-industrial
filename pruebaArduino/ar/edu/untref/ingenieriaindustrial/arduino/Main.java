package ar.edu.untref.ingenieriaindustrial.arduino;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Scanner;

public class Main {

	@SuppressWarnings("rawtypes")
	private static Enumeration portList;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out
				.print("Introduzca la cantidad de valores de temperaturas a promediar: ");
		int cantidadDeTemperaturas = sc.nextInt();

		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("COM12")) {
					//AverageTemperatureCalculator reader = new AverageTemperatureCalculator(cantidadDeTemperaturas, portId);
					//reader.startReading();
					
					AverageTemperatureReader averageTemperatureReader = new AverageTemperatureReader(cantidadDeTemperaturas, portId);
					averageTemperatureReader.startReading();
				}
			}
		}
	}

}
