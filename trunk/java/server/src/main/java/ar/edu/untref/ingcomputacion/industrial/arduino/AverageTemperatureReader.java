package ar.edu.untref.ingcomputacion.industrial.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public class AverageTemperatureReader implements SerialPortEventListener {

	private int cantidadDeTemperaturas;
	private OutputStream outputStream;
	private SerialPort serialPort;
	private InputStream inputStream;
	private StringBuilder paqueteDeLectura = new StringBuilder();
	private String[] valoresDeLosSensores;
	private double temperaturaPromedio = 22.0;
	private double humedadPromedio = 33.0;
	private double O2Promedio = 60.0;

	public AverageTemperatureReader(int cantidadDeTemperaturas, CommPortIdentifier portId) {

		this.cantidadDeTemperaturas = cantidadDeTemperaturas;

		try {
			serialPort = (SerialPort) portId.open("Simple", 2000);

			try {
				outputStream = serialPort.getOutputStream();
				inputStream = serialPort.getInputStream();
			} catch (IOException e) {
				System.out.println(e);
			}
			try {
				serialPort.addEventListener(this);
			} catch (TooManyListenersException e) {
				System.out.println(e);
			}
			try {
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				System.out.println(e);
			}
			
		} catch (PortInUseException e) {
			System.out.println(e);
		}
	}

	public void startReading() {
		
		try {
			
			serialPort.notifyOnDataAvailable(true);
			outputStream.write(this.cantidadDeTemperaturas);
			outputStream.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} finally {
			
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void serialEvent(SerialPortEvent event) {
		
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:

			try {
				byte[] readBuffer = new byte[inputStream.available()];

				while (inputStream.available() > 0) {
					inputStream.read(readBuffer);
				}

				String lectura = new String(readBuffer).trim();
				paqueteDeLectura.append(lectura);
				if (lectura.contains("-")) {
					desglosarPaqueteDeLectura();
					calcularTemperaturaPromedio(valoresDeLosSensores);
					calcularHumedadPromedio(valoresDeLosSensores);
					calcular02Promedio(valoresDeLosSensores);
					System.out.println("tempProm: " + temperaturaPromedio + " humProm: " + humedadPromedio + " 02Prom: " + O2Promedio);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	private void calcular02Promedio(String[] valoresDeLosSensores) {
		double sumatoria = 0.0;
		int cnt = 0;
		for (int i = 4; i < 5; i++) {
			if (revisarO2(valoresDeLosSensores[i])) {
				sumatoria += Double.parseDouble(valoresDeLosSensores[i]);
				cnt++;
			}else{
				generarAlerta("02", i + 1 + "");
			}
		}
		
		if (cnt != 0) {
			O2Promedio = sumatoria / cnt;
		}else{
			O2Promedio = 0.0;
		}
	}

	private void generarAlerta(String tipoSensor, String nroSensor) {
		System.out.println("El sensor de " + tipoSensor + " numero " + nroSensor + " ha fallado.");
	}

	private void calcularHumedadPromedio(String[] valoresDeLosSensores) {
		double sumatoria = 0.0;
		int cnt = 0;
		for (int i = 2; i < 4; i++) {
			if (revisarHumedad(valoresDeLosSensores[i])) {
				sumatoria += Double.parseDouble(valoresDeLosSensores[i]);
				cnt++;
			}else{
				generarAlerta("humedad", i + 1 + "");
			}
		}
		if (cnt != 0) {
			humedadPromedio = sumatoria / cnt;
		}else{
			humedadPromedio = 0.0;
		}
	}
	
	private void calcularTemperaturaPromedio(String[] valoresDeLosSensores) {
		double sumatoria = 0.0;
		int cnt = 0;
		for (int i = 0; i < 2; i++) {
			if (revisarTemperatura(valoresDeLosSensores[i])) {
				sumatoria += Double.parseDouble(valoresDeLosSensores[i]);
				cnt++;
			}else{
				generarAlerta("temperatura", (i + 1) + "");
			}
		}
		
		if (cnt != 0) {
			temperaturaPromedio = sumatoria / cnt;
		}else{
			temperaturaPromedio = 0.0;
		}
	}


	private boolean revisarTemperatura(String temperatura) {
		return true;
		
	}

	private boolean revisarHumedad(String humedad) {
		return true;
	}

	private boolean revisarO2(String O2) {
		if (!(Math.abs(temperaturaPromedio - Double.parseDouble(O2)) > 5.0 )) {
		return true;
		}else{
			return false;
		}
	}

	private void desglosarPaqueteDeLectura() {
		String paqueteDeLecturaProcesado = paqueteDeLectura.toString().replace("-", "");
		valoresDeLosSensores = (paqueteDeLecturaProcesado).split("/");
		paqueteDeLectura = new StringBuilder();
	}

}
