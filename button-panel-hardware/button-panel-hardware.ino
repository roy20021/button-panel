/**
 * Button Panel Hardware controller
 */

const int switchesNumber = 7;
const int switches[] = {50, 52, 48, 46, 44, 12, 13};
int switchesStatus[switchesNumber];

const int buttonsNumber = 3; // TODO: miss last one
const int buttons[] = {45, 22, 35}; // TODO: miss last one
int buttonsStatus[buttonsNumber];

const int ledsNumber = 3; // TODO: miss last one
const int leds[] = {47, 33, 51}; // TODO: miss last one
unsigned long ledsLastON[ledsNumber];
const unsigned long ledsTimeON = 600;

const int notToFixNumber = 2;
const int notToFix[] = { 52, 48 };

bool somethingChanged = false;
bool lastSomethingChanged = false;
unsigned long lastDebounceTime = 0;
const unsigned long debounceDelay = 80; 

char ledToTurnON = 'A';
boolean fullLoad = false;

void setup() {
Serial.begin(9600);
 while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  // Setup switches, button and leds  
  for(int i = 0; i < switchesNumber; i++){
    pinMode(switches[i], INPUT_PULLUP);
    switchesStatus[i] = -1;
  }

  for(int i = 0; i < buttonsNumber; i++){
    pinMode(buttons[i], INPUT_PULLUP);
    buttonsStatus[i] = -1;
  }

  for(int i = 0; i < ledsNumber; i++){
    pinMode(leds[i], OUTPUT);
    ledsLastON[i] = 0;
  }

  Serial.println("##Ready");
}

void loop() {
  unsigned long now = millis();

  // Check if some leds have to be turned OFF
  for(int i = 0; i < ledsNumber; i++){
    if((now - ledsLastON[i]) > ledsTimeON){
      digitalWrite(leds[i], LOW);
    }
  }

  // Turn ON request LED
  if(ledToTurnON != 'A'){
    int index = ledToTurnON - '0' - 1;
    digitalWrite(leds[index], HIGH);
    ledsLastON[index] = now;
   
    ledToTurnON = 'A';
  }

  if(fullLoad){
    fullLoad = false;
    sendStatus();
  }

  if((now - lastDebounceTime) > debounceDelay){ // Wait some time before read again
    lastDebounceTime = now;
  
    readInput(switches, switchesStatus, switchesNumber);
    readInput(buttons, buttonsStatus, buttonsNumber);  

    if(somethingChanged ) {  
     somethingChanged = false;      
     sendStatus();
    }
  } 
}

void readInput(const int pinArray[], int statusArray[], int arrayLength){
  for(int i = 0; i < arrayLength; i++){
    int currentPin = pinArray[i];
    int newStatus = fix(currentPin, digitalRead(currentPin));  
    verifyUpdate(newStatus,  statusArray[i]);
    statusArray[i] = newStatus;
  }
}

void verifyUpdate(int newState, int oldState) {
  somethingChanged = somethingChanged || (newState != oldState);
}

int fix(int pin, int state) {
  for(int i = 0; i < notToFixNumber; i++){
    if(pin == notToFix[i]){
      return state;
    }
  }
  return 1 - state; // By default fix :)
}

void sendStatus(){
     Serial.println("##Status Start");
  
     Serial.print("Switches:");
     for(int i = 0; i < switchesNumber; i++){
      Serial.print(switchesStatus[i]);
     } 
     Serial.println();

     Serial.print("Buttons:");
     for(int i = 0; i < buttonsNumber; i++){
      Serial.print(buttonsStatus[i]);
     } 
     Serial.println();

     /* Only for debug
     Serial.print("Leds:");
     for(int i = 0; i < ledsNumber; i++){
      Serial.print(" ");
      Serial.print(digitalRead(leds[i]));
     } 
     Serial.println();
     */

     Serial.println("##Status End");
}

void serialEvent() {
  while (Serial.available()) {
    char tmp = (char)Serial.read();
    if(tmp >= '1' && tmp <= '3'){ // TODO: add forth button
      ledToTurnON = tmp;
    } else if(tmp == 'S') {
      fullLoad = true;
    }
  }
}
