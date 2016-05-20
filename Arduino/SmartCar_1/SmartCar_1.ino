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
int counter = 0;
int var = 100;
int angle;
float mps, mph;
int distance;
int direction = 2;

/////////////////////////////////////////////////////

void setup() {
  Serial3.begin(9600);
  pinMode(leftPin, OUTPUT);
  pinMode(rightPin, OUTPUT);
  encoderRight.attach(encoderRightPin);
  //encoderLeft.attach(encoderLeftPin);
  encoderRight.begin();
  //encoderLeft.begin();
  front.attach(TRIGGER_PIN, ECHO_PIN);
  car.begin();
  
}

void loop(){
  distance = front.getDistance();
  counter++;
  if (counter == 1000) {
  mps = encoderRight.getSpeed();
  mph = var * mps;
  counter = 0;
  }
  
  if(angle < 180 && distance > 20 && distance < 50){
   car.setSpeed(0);
  }

  if(Serial3.available()){
    angle = Serial3.parseInt();
    
  if(angle >= 180){
      angle = angle - 270;
      direction = 0;
    }
    else if( angle < 180){
      angle = angle - 90;
      direction = 1;
    }

   else if (angle == 770){
     car.setSpeed(0);
   }
    
  }
  if(direction < 2){
    handleInput();
  }
}

//////////////////////////////////////////////////////////

/*
 * Switch case to make the car move in different directions
 * Created by Olle R.
 * Code to make the car blink when turning left and right
 * Created by Pierre L
 */

void handleInput() {
    if(direction == 1){
      if(angle < 45 && angle > -45){
        leftState = LOW;
        rightState = LOW;
        digitalWrite(rightPin, rightState);
        digitalWrite(leftPin, leftState);
      }
      else if(angle < -45){
        blinkLeft();
      }
      else if(angle > 45){
        blinkRight();
      }
      
      car.setSpeed(30);
      car.setAngle(angle);
    }
    if(direction == 0){   
      car.setSpeed(-30);
      car.setAngle(angle);
    }
    
} 

//////////////////////////////////////////////////////////

/*
 * Making the LED "blink" without using delay() function.
 * Created by Pierre L
 */

void blinkLeft() {
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
    digitalWrite(rightPin, rightState);
    digitalWrite(leftPin, leftState);

  }
}
