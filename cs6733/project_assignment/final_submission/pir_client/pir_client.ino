#include <ESP8266WiFi.h>

const char* ssid = "xxxxxxxx";
const char* password = "xxxxxxxxxxxxx";

int pir = 5;            // is D1 on the board
int pirState = LOW;     // current state of PIR sensor
char c = 0;             // received data
char command[2] = "\0"; // command

const char* ip = "172.217.167.84";
const char* host = "cs6733-220101.appspot.com";

void setup() {
  Serial.begin(115200);
  delay(10);

  // We start by connecting to a WiFi network

  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  /* Explicitly set the ESP8266 to be a WiFi-client, otherwise, it by default,
     would try to act as both a client and an access-point and could cause
     network-issues with your other WiFi-devices on your WiFi-network. */
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void loop()
{
  String url;
  Serial.print("Connecting to ");
  Serial.println(ip);

  // Use WiFiClient class to create TCP connections
  WiFiClient client;
  const int httpPort = 80;
  if (!client.connect(ip, httpPort)) {
    Serial.println("connection failed");
    return;
  }

  // read digital pin 13 for the state of PIR sensor
  pirState = digitalRead(pir);
  if (pirState == HIGH) { // PIR sensor detected movement
    // send in this format .../api/sensors/MAC_address/1|0
    url = "/api/sensors/" + WiFi.macAddress() + "/1";
    Serial.print("Requesting URL: ");
    Serial.println(url);
    // This will send the request to the server
    client.print(String("GET ") + url + " HTTP/1.1\r\n" +
               "Host: " + host + "\r\n" +
               "Connection: close\r\n\r\n");
  }
  else { // No movement is detected
    url = "/api/sensors/" + WiFi.macAddress() + "/0";
    Serial.print("Requesting URL: ");
    Serial.println(url);
    // This will send the request to the server
    client.print(String("GET ") + url + " HTTP/1.1\r\n" +
               "Host: " + host + "\r\n" +
               "Connection: close\r\n\r\n");
  }
  unsigned long timeout = millis();
  while (client.available() == 0) {
    if (millis() - timeout > 5000) {
      Serial.println(">>> Client Timeout !");
      client.stop();
      return;
    }
  }
  Serial.println();
  Serial.println("closing connection");
  delay(100);
}
