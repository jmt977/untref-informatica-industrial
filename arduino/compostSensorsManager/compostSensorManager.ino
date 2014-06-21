#include <OneWire.h>
#include <DallasTemperature.h>

/* Include the DHT11 library available at http://arduino.cc/playground/Main/DHT11Lib */
#include <dht11.h>

dht11 DHT11;

/* Define the DIO pin that will be used to communicate with the sensor */
#define DHT11_DIO 6
// Data wire is plugged into port 2 on the Arduino
#define ONE_WIRE_BUS 2

// Setup a oneWire instance to communicate with any OneWire devices (not just Maxim/Dallas temperature ICs)
OneWire oneWire(ONE_WIRE_BUS);

// Pass our oneWire reference to Dallas Temperature. 
DallasTemperature sensors(&oneWire);
int incomingByte = 0;
void setup(void)
{
  // start serial port
  Serial.begin(9600);
  pinMode(3, OUTPUT); 
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  
  double temperaturaParaEncendidoDelVentilador = 30.0;
  double temperaturaParaApagadoDelVentilador = 25.0;
	
  double humedadParaRegado = 15.0;
  double humedadParaFinDeRegado = 40.0;
	
  double cantidadOxigenoParaMezclar = 10.0;
  double cantidadOxigenoParaDejarDeMezclar = 15.0;
  
  // Start up the library
  sensors.begin();
}

void loop(void)
{ 
  
  if (Serial.available() > 0) {
	// lee el byte entrante:
	incomingByte = Serial.read();
        if (incomingByte == 49) { 
            digitalWrite(3, HIGH);   // set the LED on
        }
        if (incomingByte == 48) { 
            digitalWrite(3, LOW);    // set the LED off 
        }
        
         if (incomingByte == 50) { 
            digitalWrite(4, HIGH);   // set the LED on
        }
        if (incomingByte == 51) { 
            digitalWrite(4, LOW);    // set the LED off 
        }
        
         if (incomingByte == 52) { 
            digitalWrite(5, HIGH);   // set the LED on
        }
        if (incomingByte == 53) { 
            digitalWrite(5, LOW);    // set the LED off 
        }
        delay(1000);         	
        // dice lo que ha recibido:
  }else{
     /* Perform a read of the sensor and check if data was read OK */
  if (DHT11.read(DHT11_DIO) == DHTLIB_OK)
  {
 
          // call sensors.requestTemperatures() to issue a global temperature 
          // request to all devices on the bus
          sensors.requestTemperatures(); // Send the command to get temperatures 
          Serial.print(sensors.getTempCByIndex(0)); 
          Serial.print("/");
          Serial.print((float)DHT11.temperature, 2);
          Serial.print("/30/");
          Serial.print((float)DHT11.humidity, 2);
          Serial.print("/43");
          Serial.println("-");
  }
  
  delay(2000);
  }  
}
