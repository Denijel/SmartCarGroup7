#include <Smartcar.h>

Car car;
Odometer encoderRight;
SR04 front;

//////////////////////////////////////////////////////

/*
 * Code for LED - turn indicators
 * Code for Odometer - measure speed and distance
 * Code for Ultraonic Sensor - Stop car when close to obstacle.
 * Created by Pierre L
 */

// Number of the LED pins 
const int leftPin = 7;
const int rightPin = 4;

// Number of the Odometer pin
//const int encoderLeftPin = 2;
const int encoderRightPin = 3;


//Number of HCSR04 pins.
const int TRIGGER_PIN = 5;
const int ECHO_PIN = 6;

// state used to set the LED. 
int leftState = LOW;
int rightState = LOW;

// stores last time LED was updated.
unsigned long previousMillis = 0;

//interval at which the LED blinks (ms)
const long interval = 500; 

boolean leftBlink = false;
boolean rightBlink = false;
char character = 'l';
int var = 100;
float mps, mph;
int distance;

/////////////////////////////////////////////////////

void setup() {
  Serial3.begin(9600);
  pinMode(leftPin, OUTPUT);
  pinMode(rightPin, OUTPUT);
  encoderRight.attach(encoderRightPin);
  encoderRight.begin();
  //encoderLeft.attach(encoderLeftPin);
  //encoderLeft.begin();
  front.attach(TRIGGER_PIN, ECHO_PIN);
  car.begin();
}

void loop(){
  
  /*
   * This code was implemented to display the speed of which
   * the car is moving.
   * Created by Pierre L
   * 
   * mps = encoderRight.getSpeed();
   * mph = var * mps;
   * Serial.println("Speed (m/h)");
   * Serial.println(mph);
   * 
   */

 // gets distance from ultrasonic sensor.
  distance = front.getDistance();

 // reads input.
  if(Serial3.available()){
    character = Serial3.read();
  }

// handles input
  handleInput(character);
}

//////////////////////////////////////////////////////////

/*
 * Switch case to make the car move in different directions
 * Created by Olle R.
 * Code to make the car blink when turning left and right
 * and collision check.
 * Created by Pierre L
 */

void handleInput(char a) {

  /*
   * If any of the "forward" characters is sent, the car will
   * stop at least 20cm from an object until a reverse char
   * is issued.
   */

if(character == 'a' && distance > 20 && distance < 50){
   character = 'l';
  }
  else if(character == 'i' && distance > 10 && distance < 35){
   character = 'l';
  }
  else if(character == 'k' && distance > 10 && distance < 35){
   character = 'l';
  }
  else if(character == 'h' && distance > 10 && distance < 35){
   character = 'l';
  }
  else if(character == 'j' && distance > 10 && distance < 35){
   character = 'l';
  }
    
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
        break; 
      case 'h': //Forward hard left 
      blinkLeft();
        car.setSpeed(50);
        car.setAngle(-70);
        break;
      case 'i': //Forward light left 
      blinkLeft();
        car.setSpeed(50);
        car.setAngle(-40);
        break;
      case 'j': //Forward hard right
      blinkRight();
        car.setSpeed(50);
        car.setAngle(70);
        break;
      case 'k': //Forward light right 
      blinkRight();
        car.setSpeed(50);
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
       case 't': //Drive backwards left
        car.setSpeed(-50);
        car.setAngle(-40); 
        break; 
        case 'f': //Drive backwards right
        car.setSpeed(-50);
        car.setAngle(40); 
        break; 
      default: //Errors and unknown input will cause the car to stop.
        car.setSpeed(0);
        car.setAngle(0);
  }
}

//////////////////////////////////////////////////////////

/*
 * Making the LED "blink" without using delay().
 * This will ensure that you can still use commands during
 * the "delay".
 * Created by Pierre L
 */

void blinkLeft() {
unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    //saves the last time you blinked the LED
    previousMillis = currentMillis;

    rightState = LOW;

    //if the LED is off turn it on and vice-versa;
    if (leftState == LOW) {
      leftState = HIGH; }
      else {
        leftState = LOW; }

  //set the LED with the leftState of the variable
  //also turns of rightState if it was previously on
    digitalWrite(rightPin, rightState);
    digitalWrite(leftPin, leftState);
  }
}

void blinkRight() {
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
  //also turns of leftState if it was previously on
    digitalWrite(rightPin, rightState);
    digitalWrite(leftPin, leftState);

  }
}
