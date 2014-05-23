
#include <OneWire.h>
#include <DallasTemperature.h>
// Data wire is plugged into port 2 on the Arduino
#define ONE_WIRE_BUS 2
// Setup a oneWire instance to communicate with any OneWire devices (not just Maxim/Dallas temperature ICs)
OneWire oneWire(ONE_WIRE_BUS);
// Pass our oneWire reference to Dallas Temperature. 
DallasTemperature sensors(&oneWire);
int incomingByte = 0;
float acumulador = 0;

void setup(){
  Serial.begin(9600);
    sensors.begin();

}


void loop(){
  if ( Serial.available() > 0 ) {
   
   incomingByte = Serial.parseInt();
   Serial.print(incomingByte, DEC);
  }
  acumulador = 0; 
  int i = 0;
  for (i; i< incomingByte; i++){
  
    sensors.requestTemperatures();
    Serial.print( "temperatura " );
    Serial.println( sensors.getTempCByIndex(0) );
    acumulador += sensors.getTempCByIndex(0);
    
  }

  if ( incomingByte != 0 ){
    Serial.print( "Promedio: " );
    Serial.println(acumulador / incomingByte);
  }  
  
}
