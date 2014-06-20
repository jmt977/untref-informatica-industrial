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
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

public class CompostManager implements SerialPortEventListener {

	private OutputStream outputStream;
	private SerialPort serialPort;
	private InputStream inputStream;
	private StringBuilder paqueteDeLectura = new StringBuilder();
	private String[] valoresDeLosSensores;
	
	private double[] sensorTemperatura = new double[2];
	private double[] sensorHumedad = new double[2];
	private double[] sensorO2 = new double[1];
	
	private boolean ventiladorEncendido = false;
	private boolean regando = false;
	private boolean mezclando;
	
	private String lectura;
	private long frecuenciaDeRiego;
	private long duracionDeRiego;
	
	private double temperaturaParaEncendidoDelVentilador;
	private double temperaturaParaApagadoDelVentilador;
	
	private double humedadParaRegado;
	private double humedadParaFinDeRegado;
	
	private double cantidadOxigenoParaMezclar;
	private double cantidadOxigenoParaDejarDeMezclar;
	
	private TimerTask regarTimerTask;
	private TimerTask mezclarTimerTask;
	private TimerTask ventilarTimerTask;
	
	private Timer ventilarTimer;
	private Timer regarTimer;
	private Timer mezclarTimer;

	public CompostManager(CommPortIdentifier portId, long frecuenciaDeRiego, final long duracionDeRiego, double temperaturaParaEncendidoDelVentilador, double temperaturaParaApagadoDelVentilador, double humedadParaRegado,
			double humedadParaFinDeRegado, double cantidadOxigenoParaMezclar,
			double cantidadOxigenoParaDejarDeMezclar) {

		this.temperaturaParaEncendidoDelVentilador = temperaturaParaEncendidoDelVentilador;
		this.temperaturaParaApagadoDelVentilador = temperaturaParaApagadoDelVentilador;
		this.humedadParaRegado = humedadParaRegado;
		this.humedadParaFinDeRegado = humedadParaFinDeRegado;
		this.cantidadOxigenoParaMezclar = cantidadOxigenoParaMezclar;
		this.cantidadOxigenoParaDejarDeMezclar = cantidadOxigenoParaDejarDeMezclar;
		this.frecuenciaDeRiego = frecuenciaDeRiego;
		this.duracionDeRiego = duracionDeRiego;

		try {
			serialPort = (SerialPort) portId.open("Simple", 2000);

			try {
				outputStream = serialPort.getOutputStream();
				inputStream = serialPort.getInputStream();
				regarTimerTask = new TimerTask() {

					public void run() {

						try {
							regar();
							Thread.sleep(duracionDeRiego);
							pararDeRegar();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};
				
				ventilarTimerTask = new TimerTask() {

					public void run() {

						try {
							encenderVentilador();
							Thread.sleep(duracionDeRiego);
							apagarVentilador();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};
				
				mezclarTimerTask = new TimerTask() {

					public void run() {

						try {
							mezclar();
							Thread.sleep(duracionDeRiego);
							pararDeMezclar();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};

				ventilarTimer = new Timer();
				regarTimer = new Timer();
				mezclarTimer = new Timer();
				
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

	public void start() {
		serialPort.notifyOnDataAvailable(true);
		regarTimer.scheduleAtFixedRate(regarTimerTask, 25000, frecuenciaDeRiego);
		mezclarTimer.scheduleAtFixedRate(mezclarTimerTask, 15000, frecuenciaDeRiego);
		ventilarTimer.scheduleAtFixedRate(ventilarTimerTask, 5000, frecuenciaDeRiego);
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
					if (!regando) {
						inputStream.read(readBuffer);
					}else{
						readBuffer = new byte[inputStream.available()];
						break;
					}
				}
				lectura = new String(readBuffer).trim();
				//contador++;
				//System.out.println("LECTURA: "+ contador + " : " + lectura);
				paqueteDeLectura.append(lectura);
				if (lectura.contains("-")) {
					desglosarPaqueteDeLectura();
					System.out.println("TEMPERATURA SENSOR 1: " + sensorTemperatura[0] + " °C");
					System.out.println("TEMPERATURA SENSOR 2: " + sensorTemperatura[1] + " °C");
					System.out.println("HUMEDAD SENSOR 1: " + sensorHumedad[0] + " %");
					System.out.println("HUMEDAD SENSOR 2: " + sensorHumedad[1] + " %");
					System.out.println("O2 SENSOR 1: " + sensorO2[0] + " %");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}


	private void desglosarPaqueteDeLectura() {
		String paqueteDeLecturaProcesado = paqueteDeLectura.toString().replace("-", "");
		valoresDeLosSensores = (paqueteDeLecturaProcesado).split("/");
		
		sensorTemperatura[0] = Double.parseDouble(valoresDeLosSensores[0]);
		sensorTemperatura[1] = Double.parseDouble(valoresDeLosSensores[1]);
		
		sensorHumedad[0] = Double.parseDouble(valoresDeLosSensores[2]);
		sensorHumedad[1] = Double.parseDouble(valoresDeLosSensores[3]);
		
		sensorO2[0] = Double.parseDouble(valoresDeLosSensores[4]);
		
		paqueteDeLectura = new StringBuilder();
	}

	private void encenderVentilador() {
		try {
			System.out.println("VENTILADOR ENCENDIDO");
			ventiladorEncendido = true;
			outputStream.write(49);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void apagarVentilador() {
		try {
			ventiladorEncendido = false;
			System.out.println("VENTILADOR APAGADO");
			outputStream.write(48);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void regar() {
		try {
			if (!regando) {
				System.out.println("REGANDO");
				regando = true;
				serialPort.notifyOnDataAvailable(false);
				outputStream.write(52);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void pararDeRegar() {
		try {
			if (regando) {
				System.out.println("REGADO APAGADO");
				regando = false;
				serialPort.notifyOnDataAvailable(true);
				outputStream.write(53);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void mezclar() {
		try {
			if (!mezclando) {
				System.out.println("MEZCLANDO");
				mezclando = true;
				serialPort.notifyOnDataAvailable(true);
				outputStream.write(50);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void pararDeMezclar() {
		try {
			if (mezclando) {
				System.out.println("MEZCLADO APAGADO");
				mezclando = false;
				serialPort.notifyOnDataAvailable(true);
				outputStream.write(51);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isVentilando() {
		return this.ventiladorEncendido;
	}
	
	public boolean isRegando() {
		return this.regando;
	}
	
	public boolean isMezclando() {
		return this.mezclando;
	}

}
