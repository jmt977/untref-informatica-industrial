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
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

public class CompostManager implements SerialPortEventListener {

	private OutputStream outputStream;
	private SerialPort serialPort;
	private InputStream inputStream;
	private StringBuilder paqueteDeLectura = new StringBuilder();
	private String[] valoresDeLosSensores;

	private double[] sensoresDeTemperatura = new double[2];
	private double[] sensoresDeHumedad = new double[2];
	private double[] sensoresDeO2 = new double[1];

	private boolean ventiladorEncendido = false;
	private boolean regando = false;
	private boolean mezclando;

	private String lectura;

	private long frecuenciaDeRiego;
	private long duracionDeRiego;
	private long frecuenciaDeMezclado;
	private long duracionDeMezclado;
	private long frecuenciaDeVentilado;
	private long duracionDeVentilado;

	private double temperaturaParaEncendidoDelVentilador;
	private double temperaturaParaApagadoDelVentilador;

	private double humedadParaRegado;
	private double humedadParaFinDeRegado;

	private double cantidadOxigenoParaMezclar;
	private double cantidadOxigenoParaDejarDeMezclar;

	private boolean realizandoMantenimiento = false;
	private boolean realizandoAjusteTemperatura = false;
	private boolean realizandoAjusteHumedad = false;
	private boolean realizandoAjusteO2 = false;

	private TimerTask regarTimerTask;
	private TimerTask mezclarTimerTask;
	private TimerTask ventilarTimerTask;

	private Timer ventilarTimer;
	private Timer regarTimer;
	private Timer mezclarTimer;

	public CompostManager(CommPortIdentifier portId, long frecuenciaDeRiego, final long duracionDeRiego, long frecuenciaDeMezclado, final long duracionDeMezclado, long frecuenciaDeVentilado, final long duracionDeVentilado, double temperaturaParaEncendidoDelVentilador, double temperaturaParaApagadoDelVentilador, double humedadParaRegado,
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
		this.frecuenciaDeMezclado = frecuenciaDeMezclado;
		this.duracionDeMezclado = duracionDeMezclado;
		this.frecuenciaDeVentilado = frecuenciaDeVentilado;
		this.duracionDeVentilado = duracionDeVentilado;

		try {
			serialPort = (SerialPort) portId.open("Simple", 2000);

			try {
				outputStream = serialPort.getOutputStream();
				inputStream = serialPort.getInputStream();

				//definimos los timer para cada actividad: regar, ventilar y mezclar. 
				regarTimerTask = new TimerTask() {

					public void run() {
						if (!realizandoAjusteHumedad && !regando) {
							try {
								realizandoMantenimiento = true;
								System.out.println("TAREA DE MANTENIMIENTO, SE PROCEDE A REGAR");
								regar();
								Thread.sleep(duracionDeRiego);
								pararDeRegar();
								realizandoMantenimiento = false;
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				};

				ventilarTimerTask = new TimerTask() {

					public void run() {
						if (!realizandoAjusteTemperatura && !ventiladorEncendido) {
							try {
								realizandoMantenimiento = true;
								System.out.println("TAREA DE MANTENIMIENTO, SE PROCEDE A VENTILAR");
								encenderVentilador();
								Thread.sleep(duracionDeRiego);
								apagarVentilador();
								realizandoMantenimiento = false;
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				};

				mezclarTimerTask = new TimerTask() {

					public void run() {
						if (!realizandoAjusteO2 && !mezclando) {
							try {
								realizandoMantenimiento = true;
								System.out.println("TAREA DE MANTENIMIENTO, SE PROCEDE A MEZCLAR");
								mezclar();
								Thread.sleep(duracionDeRiego);
								pararDeMezclar();
								realizandoMantenimiento = false;
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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

		//Iniciamos las tareas de mantenimiento
		regarTimer.scheduleAtFixedRate(regarTimerTask, duracionDeRiego, frecuenciaDeRiego);
		mezclarTimer.scheduleAtFixedRate(mezclarTimerTask, duracionDeMezclado, frecuenciaDeMezclado);
		ventilarTimer.scheduleAtFixedRate(ventilarTimerTask, duracionDeVentilado, frecuenciaDeVentilado);
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
				paqueteDeLectura.append(lectura);

				/*Si recibimos un guion, entonces el paquete de lectura llegó 
				completo y lo desglosamos*/
				if (!lectura.isEmpty() && String.valueOf(lectura.charAt(lectura.length() - 1)).equals("-")) {
					desglosarPaqueteDeLectura();

					System.out.println("TEMPERATURA SENSOR 1: " + sensoresDeTemperatura[0] + " °C");
					System.out.println("TEMPERATURA SENSOR 2: " + sensoresDeTemperatura[1] + " °C");
					System.out.println("HUMEDAD SENSOR 1: " + sensoresDeHumedad[0] + " %");
					System.out.println("HUMEDAD SENSOR 2: " + sensoresDeHumedad[1] + " %");
					System.out.println("O2 SENSOR 1: " + sensoresDeO2[0] + " %");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}


	private synchronized void desglosarPaqueteDeLectura() {
		int cantidadDeRepeticionesDelCaracterDeCorteDeLinea = new StringTokenizer(paqueteDeLectura.toString() , "-").countTokens();
		//A veces se desincroniza y envia dos lineas juntas
		if (cantidadDeRepeticionesDelCaracterDeCorteDeLinea == 1) {
			String paqueteDeLecturaProcesado = paqueteDeLectura.toString().replace("-", "");

			System.out.println(paqueteDeLecturaProcesado);

			valoresDeLosSensores = (paqueteDeLecturaProcesado).split("/");

			sensoresDeTemperatura[0] = Double.parseDouble(valoresDeLosSensores[0]);
			sensoresDeTemperatura[1] = Double.parseDouble(valoresDeLosSensores[1]);

			sensoresDeHumedad[0] = Double.parseDouble(valoresDeLosSensores[2]);
			sensoresDeHumedad[1] = Double.parseDouble(valoresDeLosSensores[3]);

			sensoresDeO2[0] = Double.parseDouble(valoresDeLosSensores[4]);

			controlarValores();
			paqueteDeLecturaProcesado = new String();
			paqueteDeLectura = new StringBuilder();
		}else{
			paqueteDeLectura = new StringBuilder();
		}
	}

	private void controlarValores() {

		for (int i = 0; i < sensoresDeTemperatura.length; i++) {
			if (sensoresDeTemperatura[i] > temperaturaParaEncendidoDelVentilador && !ventiladorEncendido && !realizandoMantenimiento) {
				System.out.println("EL COMPOST HA SUPERADO LA TEMPERATURA RECOMENDADA, SE PROCEDE A VENTILAR");
				realizandoAjusteTemperatura = true;
				encenderVentilador();
			}else if (sensoresDeTemperatura[i] <= temperaturaParaApagadoDelVentilador && ventiladorEncendido && !realizandoMantenimiento) {
				System.out.println("EL COMPOST HA RECUPERADO LA TEMPERATURA RECOMENDADA");
				apagarVentilador();
				realizandoAjusteTemperatura = false;
			}
		}

		for (int i = 0; i < sensoresDeHumedad.length; i++) {
			if (sensoresDeHumedad[i] < humedadParaRegado && !regando && !realizandoMantenimiento) {
				System.out.println("SE HA DETECTADO MENOS HUMEDAD DE LA RECOMENDADA EN EL COMPOST, SE PROCEDE A REGAR");
				realizandoAjusteHumedad = true;
				regar();
			}else if (sensoresDeHumedad[i] >= humedadParaFinDeRegado && regando && !realizandoMantenimiento) {
				System.out.println("EL COMPOST HA RECUPERADO LA HUMEDAD RECOMENDADA");
				pararDeRegar();
				realizandoAjusteHumedad = false;
			}
		}

		if (sensoresDeO2[0] < cantidadOxigenoParaMezclar && !mezclando && !realizandoMantenimiento) {
			System.out.println("EL COMPOST SE ENCUENTRA CON NIVELES DE OXIGENO BAJOS, SE PROCEDE A MEZCLAR");
			realizandoAjusteO2 = true;
			mezclar();
		}else if (sensoresDeO2[0] > cantidadOxigenoParaDejarDeMezclar && mezclando && !realizandoMantenimiento) {
			System.out.println("EL COMPOST HA RECUPERADO LOS NIVELES DE OXIGENO RECOMENDADOS");
			pararDeMezclar();
			realizandoAjusteO2 = false;
		}


	}

	public void encenderVentilador() {
		try {
			System.out.println("VENTILADOR ENCENDIDO");
			ventiladorEncendido = true;

			//los parámetros numéricos corresponden a los dsitintos actuadores en el arduino
			outputStream.write(49); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void apagarVentilador() {
		try {
			ventiladorEncendido = false;
			System.out.println("VENTILADOR APAGADO");
			outputStream.write(48);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void regar() {
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

	public void pararDeRegar() {
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

	public void mezclar() {
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

	public void pararDeMezclar() {
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

	public double[] getSensoresDeTemperatura() {
		return sensoresDeTemperatura;
	}

	public double[] getSensoresDeHumedad() {
		return sensoresDeHumedad;
	}

	public double[] getSensoresDeO2() {
		return sensoresDeO2;
	}

	public void setTemperaturaParaEncendidoDelVentilador(
			double temperaturaParaEncendidoDelVentilador) {
		this.temperaturaParaEncendidoDelVentilador = temperaturaParaEncendidoDelVentilador;
	}

	public void setTemperaturaParaApagadoDelVentilador(
			double temperaturaParaApagadoDelVentilador) {
		this.temperaturaParaApagadoDelVentilador = temperaturaParaApagadoDelVentilador;
	}

	public void setHumedadParaRegado(double humedadParaRegado) {
		this.humedadParaRegado = humedadParaRegado;
	}

	public void setHumedadParaFinDeRegado(double humedadParaFinDeRegado) {
		this.humedadParaFinDeRegado = humedadParaFinDeRegado;
	}

	public void setCantidadOxigenoParaMezclar(double cantidadOxigenoParaMezclar) {
		this.cantidadOxigenoParaMezclar = cantidadOxigenoParaMezclar;
	}

	public void setCantidadOxigenoParaDejarDeMezclar(
			double cantidadOxigenoParaDejarDeMezclar) {
		this.cantidadOxigenoParaDejarDeMezclar = cantidadOxigenoParaDejarDeMezclar;
	}

}
