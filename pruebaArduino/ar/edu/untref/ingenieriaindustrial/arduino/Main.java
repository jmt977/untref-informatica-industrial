package ar.edu.untref.ingenieriaindustrial.arduino;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

public class Main {

	@SuppressWarnings("rawtypes")
	private static Enumeration portList;

	public static void main(String[] args) {

		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("COM6")) {
					CompostManager compostManager = new CompostManager(portId, 20000, 5000, 30000, 5000, 40000, 5000, 30.0, 29.0, 15.0, 40.0, 10.0, 15.0);
					compostManager.start();
				}
			}
		}
	}

}
