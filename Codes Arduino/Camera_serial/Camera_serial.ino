unsigned int image[25];
unsigned int width = 5;
unsigned int height = 5;

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
  delay(10);
  for(int ligne=0; ligne<width; ligne++){
    ChangeDigital(ligne);
    for(int colonne=0; colonne<height; colonne++){
      image[ligne*width + colonne] = analogRead(colonne);
    }
  }
  TransmissionSerial();
}

void TransmissionSerial(){
  String EncodePourSerial = "";
  boolean premierPassage = true;
  for(int ligne=0; ligne<width; ligne++){
    for(int colonne=0; colonne<height; colonne++){
      if(premierPassage){
        EncodePourSerial = EncodePourSerial + image[ligne*width + colonne];
        premierPassage = false;
      }else{
        EncodePourSerial = EncodePourSerial + "-" + image[ligne*width + colonne];
      }
    }
  }
  Serial.println(EncodePourSerial);
}

void ChangeDigital(int ligne){
  switch(ligne){
    case 0:
      digitalWrite(2, HIGH);
      digitalWrite(6, LOW);
      break;
    case 1:
      digitalWrite(3, HIGH);
      digitalWrite(2, LOW);
      break;
    case 2:
      digitalWrite(4, HIGH);
      digitalWrite(3, LOW);
      break;
    case 3:
      digitalWrite(5, HIGH);
      digitalWrite(4, LOW);
      break;
    case 4:
      digitalWrite(6, HIGH);
      digitalWrite(5, LOW);
      break;
  }
}

