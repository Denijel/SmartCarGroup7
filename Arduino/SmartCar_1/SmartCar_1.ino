#include <Smartcar.h>

Car car;

//////////////////////////////////////////////////////

/*
 * Code for LED - turn indicators
 * Created by Pierre L
 */

// Number of the LED pins 
const int leftPin = 7;
const int rightPin = 4;

// state used to set the LED. Variables will change
int leftState = LOW;
int rightState = LOW;

// stores last time LED was updated.
unsigned long previousMillis = 0;

//interval at which the LED blinks (ms)
const long interval = 500; 

boolean leftBlink = false;
boolean rightBlink = false;
char character = 'l';

/////////////////////////////////////////////////////

void setup() {
  Serial3.begin(9600);
  pinMode(leftPin, OUTPUT);
  pinMode(rightPin, OUTPUT);
  car.begin();
}

void loop(){
  if(Serial3.available()){
    character = Serial3.read();
  }
  handleInput(character);
}

//////////////////////////////////////////////////////////

/*
 * Switch case which takes the output from the joystick
 * Created by Olle R.
 */

void handleInput(char a) {

    
    switch (a) {
      case 'a': //Drive forward
        car.setSpeed(30);
        car.setAngle(0);
        leftState = LOW;
        rightState = LOW;
        digitalWrite(rightPin, rightState);
        digitalWrite(leftPin, leftState);
        break; 
      case 'b': //Drive backwards
        car.setSpeed(-30);
        car.setAngle(0); 
        leftState = HIGH;
        rightState = HIGH;
        digitalWrite(rightPin, rightState);
        digitalWrite(leftPin, leftState);
        break; 
      case 'h': //Forward hard left 
      turnLeft();
        car.setSpeed(30);
        car.setAngle(-70);
        break;
      case 'i': //Forward light left 
      turnLeft();
        car.setSpeed(30);
        car.setAngle(-40);
        break;
      case 'j': //Forward hard right
      turnRight();
        car.setSpeed(30);
        car.setAngle(70);
        break;
      case 'k': //Forward light right 
      turnRight();
        car.setSpeed(30);
        car.setAngle(40);
        break;
      case 'l': //If joystick is in the middle, stop the car.
        car.setSpeed(0);
        car.setAngle(0);
        leftState = LOW;
        rightState = LOW;
        digitalWrite(rightPin, rightState);
        digitalWrite(leftPin, leftState);
        break;
      default: //Errors and unknown input will cause the car to stop.
        car.setSpeed(0);
        car.setAngle(0);
    
  }
}

//////////////////////////////////////////////////////////

/*
 * Making the LED "blink" without using delay().
 * Created by Pierre L
 */

void turnLeft() {
unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    //save the last time you blinked the LED
    previousMillis = currentMillis;

    rightState = LOW;

    //if the LED is off turn it on and vice-versa;
    if (leftState == LOW) {
      leftState = HIGH; }
      else {
        leftState = LOW; }

  //set the LED with the leftState of the variable
    digitalWrite(rightPin, rightState);
    digitalWrite(leftPin, leftState);
  }
}

void turnRight() {
unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    //save the last time you blinked the LED
    previousMillis = currentMillis;

    leftState = LOW;

    //if the LED is off turn it on and vice-versa;
    if (rightState == LOW) {
      rightState = HIGH; }
      else {
        rightState = LOW; }
  
  //set the LED with the rightState of the variable
    digitalWrite(rightPin, rightState);
    digitalWrite(leftPin, leftState);

  }
}
