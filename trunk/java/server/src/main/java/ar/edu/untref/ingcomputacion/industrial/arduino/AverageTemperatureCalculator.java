package ar.edu.untref.ingcomputacion.industrial.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

public class AverageTemperatureCalculator implements Runnable, SerialPortEventListener {

	private int cantidadDeTemperaturas;
	private int contador;
	private Double acumulado = new Double(0);
	private StringBuilder sb = new StringBuilder();
	private InputStream inputStream;
	private Thread readThread;

	public AverageTemperatureCalculator(int cantidadDeTemperaturas, CommPortIdentifier portId) {

		SerialPort serialPort;
		this.cantidadDeTemperaturas = cantidadDeTemperaturas;

		try {
			serialPort = (SerialPort) portId.open("_18b20", 2000);

			try {
				inputStream = serialPort.getInputStream();
			} catch (IOException e) {
				System.out.println(e);
			}
			try {
				serialPort.addEventListener(this);
			} catch (TooManyListenersException e) {
				System.out.println(e);
			}
			serialPort.notifyOnDataAvailable(true);
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

		readThread = new Thread(this);
		readThread.start();
	}

	public void run() {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			System.out.println(e);
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
			byte[] readBuffer = new byte[40];

			try {

				while (inputStream.available() > 0) {
					inputStream.read(readBuffer);
				}

				String lectura = new String(readBuffer).trim();

				if (!lectura.replace("-", "").isEmpty()) {
					sb.append(lectura.replace("-", ""));
				}

				if (lectura.contains("-")) {

					acumulado += Double.parseDouble(sb.toString());
					contador++;
					System.out.println("temp " + sb);
					sb = new StringBuilder();

					if (contador == cantidadDeTemperaturas) {

						System.out.println("promedio: " + acumulado
								/ cantidadDeTemperaturas);
						contador = 0;
						acumulado = 0d;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}
}