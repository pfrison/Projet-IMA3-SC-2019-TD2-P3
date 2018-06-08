#include <stdlib.h>

unsigned int image[4];
static byte c1;
int maximum = 0;
int minimum = 2000;

void setup() {
  Serial.begin(9600);
  
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  
  digitalWrite(2, LOW);
  digitalWrite(3, LOW);
  digitalWrite(4, LOW);
  digitalWrite(5, LOW);
  digitalWrite(6, LOW);
}

void loop() {
  delay(1000);
  for(int ligne=0; ligne<2; ligne++){
    ChangeDigital(ligne);
    for(int colonne=0; colonne<2; colonne++){
      image[ligne*2 + colonne] = analogRead(colonne);
    }
  }
  AfficherImage();
}

void AfficherImage(){
  for(int ligne=0; ligne<2; ligne++){
    for(int colonne=0; colonne<2; colonne++){
      int pixel = image[ligne*2 + colonne];
      if(maximum < pixel){maximum = pixel;}
      if(minimum > pixel){minimum = pixel;}
    }
  }
  
  for(int ligne=0; ligne<2; ligne++){
    for(int i=0; i<2; i++){
      for(int colonne=0; colonne<2; colonne++){
        for(int k=0; k<5; k++){
          Serial.print(Contraste(minimum , maximum, image[ligne*5 + colonne]));
        }
      }
      Serial.println("");
    }
  }
  Serial.println("");
}

String Contraste(int min, int max, int val){
  double mind = min;
  double maxd = max;
  double vald = val;
  double cont = 1-((vald-mind)/(maxd-mind));
  if(cont > 0.9){
    return "#";
  }else if(cont > 0.8){
    return "$";
  }else if(cont > 0.7){
    return "X";
  }else if(cont > 0.6){
    return "x";
  }else if(cont > 0.5){
    return "=";
  }else if(cont > 0.4){
    return "+";
  }else if(cont > 0.3){
    return ";";
  }else if(cont > 0.2){
    return ":";
  }else if(cont > 0.1){
    return ".";
  }else{
    return " ";
  }
}

void ChangeDigital(int ligne){
  switch(ligne){
    case 0:
      digitalWrite(2, HIGH);
      digitalWrite(6, LOW);
      break;
    case 1:
      digitalWrite(2, LOW);
      digitalWrite(3, HIGH);
      break;
    case 2:
      digitalWrite(3, LOW);
      digitalWrite(4, HIGH);
      break;
    case 3:
      digitalWrite(4, LOW);
      digitalWrite(5, HIGH);
      break;
    case 4:
      digitalWrite(5, LOW);
      digitalWrite(6, HIGH);
      break;
  }
}

