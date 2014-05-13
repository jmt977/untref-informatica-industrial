package ar.edu.untref.ingenieriaindustrial.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public class SimpleRead implements Runnable, SerialPortEventListener {
	static CommPortIdentifier portId;
	static Enumeration portList;

	InputStream inputStream;
	SerialPort serialPort;
	Thread readThread;

	public static void main(String[] args) {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("COM3")) {
					// if (portId.getName().equals("/dev/term/a")) {
					SimpleRead reader = new SimpleRead();
				}
			}
		}
	}

	public SimpleRead() {
		try {
			serialPort = (SerialPort) portId.open("_18b20", 2000);
		} catch (PortInUseException e) {
			System.out.println(e);
		}
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
			serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			System.out.println(e);
		}
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
					int numBytes = inputStream.read(readBuffer);
				}
				
				String lectura = new String(readBuffer).trim();
				if (lectura.contains("-")){
				
					System.out.println(lectura);
					
				}else{
					
					System.out.print(lectura);
				}
			} catch (IOException e) {
				System.out.println(e);
			}
			break;
		}
	}
}