#include <OneWire.h>
#include <DallasTemperature.h>

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
  pinMode(13, OUTPUT); 
  // Start up the library
  sensors.begin();
}

void loop(void)
{ 
  
  if (Serial.available() > 0) {
	// lee el byte entrante:
	incomingByte = Serial.read();
        if (incomingByte == 49) { 
            digitalWrite(13, HIGH);   // set the LED on
        }
        if (incomingByte == 48) { 
            digitalWrite(13, LOW);    // set the LED off 
        }
        delay(1000);         	
        // dice lo que ha recibido:
  }else{
          // call sensors.requestTemperatures() to issue a global temperature 
          // request to all devices on the bus
          sensors.requestTemperatures(); // Send the command to get temperatures 
          Serial.print(sensors.getTempCByIndex(0)); 
          Serial.print("/");
          Serial.print(sensors.getTempCByIndex(3));
          Serial.print("/30/32/43");
          Serial.println("-");
  }
  
  delay(2000);  
}
