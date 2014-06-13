package ar.edu.untref.ingenieriaindustrial.arduino;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

public class Main {

	@SuppressWarnings("rawtypes")
	private static Enumeration portList;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("COM5")) {
					CompostManager averageTemperatureReader = new CompostManager(portId, 20000, 5000, 30.0, 25.0);
					averageTemperatureReader.start();
				}
			}
		}
	}

}
