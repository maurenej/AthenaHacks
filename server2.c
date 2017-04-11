#include <stdio.h>
#include <unistd.h>
#include <signal.h>
#include <time.h>
#include <math.h>
#include <mraa/aio.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <pthread.h>

#include "rgb_lcd.h"

#define TOUCH_PIN 4
#define ALARM_PIN 8

sig_atomic_t volatile tempFlag = 0;
sig_atomic_t volatile lightFlag = 0;
sig_atomic_t volatile soundFlag = 0;
sig_atomic_t volatile autoFlag = 0;

sig_atomic_t volatile displayTemp = 0;
sig_atomic_t volatile displayTime = 0;

char scale = 'F';
float temperature = 75.6;

mraa_aio_context tempSensor;
mraa_aio_context lightSensor;
mraa_gpio_context ledPin;
mraa_gpio_context alarmPin;
mraa_gpio_context touchSensor;
mraa_gpio_context buzzer;
mraa_i2c_context rgbDisplay;

int red;
int blue;
int green;


void* constFunction() 
{
// For touch sensor
   while(1) {
      //printf("%d", soundFlag);
      //touch sensor
      uint16_t value = mraa_gpio_read(touchSensor);
      mraa_gpio_write(alarmPin, value);

      //displaying
      setRGB(red, blue, green);
      setCursor(0,0);
      char firstLine[16];
      sprintf(firstLine, "Ohm Sweet Ohm   ");
      int j = 0;
      for (j = 0; j < 16; j++) 
         writeFunction(firstLine[j]);
      setCursor(0,1);
      char secondLine[16];
      time_t timestamp;
      struct tm* timeStruct;
      char buff[10];
      time(&timestamp);
      int i = 0;
      timeStruct = localtime(&timestamp);
      strftime(buff, 10, "%H:%M", timeStruct);
      /*if (!displayTemp && !displayTime) {
         sprintf(secondLine, "                ");
         for (i = 0; i < 16; i++) 
            writeFunction(secondLine[i]);
      }
      else*/ if (displayTemp && displayTime) {
         //printf("ENTERED FOR BOTH\n");
         sprintf(secondLine, "%.1f%c %s     ", temperature, scale, buff);
         for (i = 0; i < 16; i++) 
            writeFunction(secondLine[i]);
      }
      else if (displayTemp) {
         //char secondLine[5];
         sprintf(secondLine, "%.1f%c           ", temperature, scale);
         for (i = 0; i < 16; i++) 
            writeFunction(secondLine[i]);
      }
      else if (displayTime) {
         //char secondLine[5];
         sprintf(secondLine, "%s           ", buff);
         for (i = 0; i < 16; i++) 
            writeFunction(secondLine[i]);
      }
      else {
         sprintf(secondLine, "                ");
         for (i = 0; i < 16; i++) 
            writeFunction(secondLine[i]);
      }
      memset(secondLine, 0, 16);
   }
}

void* doprocessing (void* in_sock) {
   int sock = (int) in_sock;
   int n;
   char buffer[256];
   bzero(buffer,256);
   n = read(sock,buffer,255);
   
   if (n < 0) {
      perror("ERROR reading from socket");
      exit(1);
   }
   
   printf("Here is the message: %s\n",buffer);
   n = write(sock,"I got your message",18);
   
   if (n < 0) {
      perror("ERROR writing to socket");
      exit(1);
   }
   if (strncmp(buffer, "TEMP_C", 6) == 0){
      tempFlag = 1;
      scale = 'C';
      displayTemp = 1;
   }
   else if (strncmp(buffer, "TEMP_F", 6) == 0) {
      tempFlag = 1;
      scale = 'F';
      displayTemp = 1;
   }
   else if (strncmp(buffer, "TEMP_OFF", 8) == 0) {
      tempFlag = 0;
      displayTemp = 0;
   }
   else if (strncmp(buffer, "TIME_ON", 7) == 0) {
      displayTime = 1;
      printf("Entered TIME_ON\n");
   }
   else if (strncmp(buffer, "TIME_OFF", 8) == 0) {
      displayTime = 0;
   }
   else if (strncmp(buffer, "LIGHT_ON", 8) == 0) {
      lightFlag = 1;
   }
   else if (strncmp(buffer, "LIGHT_OFF", 9) == 0) {
      lightFlag = 0;
   }
   else if (strncmp(buffer, "SOUND_ON", 8) == 0) {
      soundFlag = 1;
   }
   else if (strncmp(buffer, "AUTO_ON", 7) == 0) {
      autoFlag = 1;
   }
   else if (strncmp(buffer, "AUTO_OFF", 8) == 0) {
      autoFlag = 1;
   }
   else if (strncmp(buffer, "ALARM", 5) == 0) {
      //printf("ENTERED!\n");
      mraa_gpio_write(buzzer, 1);
      //printf("ENTERED1!\n");
      sleep(1);
      // printf("ENTERED2!\n");
      mraa_gpio_write(buzzer, 0);
      //printf("ENTERED3!\n");
      soundFlag = 0;
   }
   // TIME_ON, 
   else if (strncmp(buffer, "RGB_", 4) == 0) {
      char redTemp[4] = {0};
      char blueTemp[4]= {0};
      char greenTemp[4]= {0};
      strncpy(redTemp, buffer+4, 3);
      red = atoi(redTemp);
      strncpy(blueTemp, buffer+8, 3);
      blue = atoi(blueTemp);
      strncpy(greenTemp, buffer+12, 3);
      green = atoi(greenTemp);
   }
   // if (soundFlag) {
         
   //    mraa_gpio_write(buzzer, 1);
   //    sleep(1);
   //    mraa_gpio_write(buzzer, 0);
   //    soundFlag = 0;
   // }
   //constFunction();

   return NULL;
}

void* doloop(void* in_sock) {
   uint16_t value;
   while (1) {
      if (tempFlag) {
         value = mraa_aio_read(tempSensor);
         time_t timestamp;
         struct tm* timeStruct;
         char buff[10];

         time(&timestamp);
         timeStruct = localtime(&timestamp);
         strftime(buff, 10, "%H:%M", timeStruct);
      
         float R = 1023.0 / ((float)value) - 1.0;
         R = 100000.0 * R;
         temperature = 1.0/(log(R/100000.0)/4275 + 1/298.15) - 273.15;
         if (scale == 'F')
            temperature = temperature * 1.8 + 32;
         //printf("%d %s %.1f %c\n", (int) in_sock, buff, temperature, scale);
         // fprintf(file, "%s %.1f\n", buff, temperature);
         // fflush(file);
      }
      if (autoFlag) {
         uint16_t value;
         lightSensor = mraa_aio_init(1);
         while(autoFlag || lightFlag) {
            value = mraa_aio_read(lightSensor);
            if (value < 20) 
               mraa_gpio_write(ledPin, 1);
            else if (lightFlag == 1) 
               mraa_gpio_write(ledPin, 1);
            else
               mraa_gpio_write(ledPin, 0);
         }
         while(!lightFlag)
            mraa_gpio_write(ledPin, 0);
      }
      else { 
         mraa_gpio_dir(ledPin, MRAA_GPIO_OUT);
         mraa_gpio_write(ledPin, lightFlag);
      }
      sleep(1);
   }
}

int main( int argc, char *argv[] ) {
   int sockfd, newsockfd, portno, clilen;
   char buffer[256];
   struct sockaddr_in serv_addr, cli_addr;
   int n, pid;

   tempSensor = mraa_aio_init(0);
   ledPin = mraa_gpio_init(5);
   buzzer = mraa_gpio_init(3);
   touchSensor = mraa_gpio_init(TOUCH_PIN);
   alarmPin = mraa_gpio_init(ALARM_PIN);
   rgbDisplay = mraa_i2c_init(0);
   setDisplay(&rgbDisplay);
   begin(16,2);


   mraa_gpio_dir(ledPin, MRAA_GPIO_OUT);
   mraa_gpio_dir(alarmPin, MRAA_GPIO_OUT); 
   mraa_gpio_dir(buzzer, MRAA_GPIO_OUT);

   red = 050;
   blue = 125;
   green = 130;
   setRGB(red, blue, green);  
   //050,125,130
   
   /* First call to socket() function */
   sockfd = socket(AF_INET, SOCK_STREAM, 0);
   
   if (sockfd < 0) {
      perror("ERROR opening socket");
      exit(1);
   }
   
   /* Initialize socket structure */
   bzero((char *) &serv_addr, sizeof(serv_addr));
   portno = 5001;
   
   serv_addr.sin_family = AF_INET;
   serv_addr.sin_addr.s_addr = INADDR_ANY;
   serv_addr.sin_port = htons(portno);
   
   /* Now bind the host address using bind() call.*/
   if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
      perror("ERROR on binding");
      exit(1);
   }
   
   /* Now start listening for the clients, here
      * process will go in sleep mode and will wait
      * for the incoming connection
   */
   
   listen(sockfd,5);
   clilen = sizeof(cli_addr);
   
   pthread_t constid;
   pthread_create(&constid, NULL, &constFunction, NULL);
   while (1) {
      //constFunction();
      newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);
		
      if (newsockfd < 0) {
         perror("ERROR on accept");
         exit(1);
      }
      
      pthread_t tid;
      pthread_create(&tid, NULL, &doprocessing, (void*)newsockfd);
      pthread_create(&tid, NULL, &doloop, (void*)newsockfd);
      //pthread_create(&tid, NULL, &constFunction, (void*)newsockfd);

	   pthread_detach(tid);

   } /* end of while */
}