#include <Smartcar.h>

Car car;

// Number of the LED pins
const int leftPin = 7;
const int rightPin = 8;

// state used to set the LED. Variables will change
int leftState = LOW;
int rightState = LOW;

// stores last time LED was updated.
unsigned long previousMillis = 0;


//interval at which the LED blinks (ms)
const long interval = 500; 

boolean leftBlink = false;
boolean rightBlink = false;


void setup() {
  Serial.begin(9600);
  pinMode(leftPin, OUTPUT);
  pinMode(rightPin, OUTPUT);
  car.begin();
}

void loop() {
  handleInput();

 /* while (leftBlink) {
    turnLeft();
  }
  while (rightBlink) {
    turnRight();
  } */
}

void handleInput() {
  if (Serial.available()) {
    char input;
    while (Serial.available()) input = Serial.read();
    switch (input) {
      case 'a': //Drive forward
        car.setSpeed(30);
        car.setAngle(0);
        break; 
      case 'b': //Drive backwards
        car.setSpeed(-30);
        car.setAngle(0); 
        break; 
      case 'c':  //Backwards hard left
        turnLeft();
        car.setSpeed(-30);
        car.setAngle(-70);
        break;
      case 'd':  //Backwards light left
        leftBlink = true;
        car.setSpeed(-30);
        car.setAngle(-40);
        break;
      case 'e': //Backwards hard right
        car.setSpeed(-30);
        car.setAngle(-110);
        turnRight();
        break;
      case 'f': //Backwards light right
        car.setSpeed(-30);
        car.setAngle(-140);
        rightBlink = true;
        break;
      case 'h': //Forward hard left 
        car.setSpeed(30);
        car.setAngle(70);
        leftBlink = true;
        break;
      case 'i': //Forward light left 
        car.setSpeed(30);
        car.setAngle(40);
        leftBlink = true;
        break;
      case 'j': //Forward hard right
        car.setSpeed(30);
        car.setAngle(110);
        rightBlink = true; 
        break;
      case 'k': //Forward light right 
        car.setSpeed(40);
        car.setAngle(140);
        rightBlink = true;
        break;
      case 'l': //If joystick is in the middle, stop the car.
        car.setSpeed(0);
        car.setAngle(0);
        break;
      default: //Errors and unknown input will cause the car to stop.
        car.setSpeed(0);
        car.setAngle(0);
    }
  }
}

void turnLeft() {
unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    //save the last time you blinked the LED
    previousMillis = currentMillis;

    //if the LED is off turn it on and vice-versa;
    if (leftState == LOW) {
      leftState = HIGH; }
      else {
        leftState = LOW; }

  //set the LED with the leftState of the variable
  digitalWrite(leftPin, leftState);
  }
}

void turnRight() {
unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    //save the last time you blinked the LED
    previousMillis = currentMillis;

    //if the LED is off turn it on and vice-versa;
    if (rightState == LOW) {
      rightState = HIGH; }
      else {
        rightState = LOW; }
  
  //set the LED with the rightState of the variable
  digitalWrite(rightPin, rightState);
  }
}
