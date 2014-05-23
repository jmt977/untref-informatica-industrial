package ar.edu.untref.ingenieriaindustrial.arduino;

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
	private Thread readThread;
	private SerialPort serialPort;
	private InputStream inputStream;


	public AverageTemperatureReader(int cantidadDeTemperaturas, CommPortIdentifier portId) {

		this.cantidadDeTemperaturas = cantidadDeTemperaturas;

		try {
			serialPort = (SerialPort) portId.open("sketch_may22a", 2000);

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
	

	@Override
	public void serialEvent(SerialPortEvent event) {
		
		System.err.println("entro al switch");
		
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
				System.out.println("recibi: " + lectura);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

}
