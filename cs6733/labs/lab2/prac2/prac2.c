/**
 * \file
 *         A TCP socket echo server. Listens and replies on port 8080
 * \author
 *         mds
 */

#include "contiki-net.h"
#include "sys/cc.h"
#include "buzzer.h"
#include "dev/serial-line.h"
#include "dev/cc26xx-uart.h"
#include "dev/leds.h"
#include "sys/ctimer.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "sys/etimer.h"
#include "sys/ctimer.h"
#include "dev/watchdog.h"
#include "random.h"
#include "board-peripherals.h"

#include "ti-lib.h"

static struct ctimer sensor_timer;		//Callback timer

#define SERVER_PORT 8080

static struct tcp_socket socket;

#define INPUTBUFSIZE 400
static uint8_t inputbuf[INPUTBUFSIZE];

#define OUTPUTBUFSIZE 400
static uint8_t outputbuf[OUTPUTBUFSIZE];

static struct ctimer timer;
static struct ctimer timer1;

static char leds_clr='a';
static int led_on=0;

static const int FREQ = 1000;
static int  buz_state = 0;
static int  buz_freq = 0;

static int count = 0;
static int pressure_val;

static int humidity_val;
static char str[80];

//Intialise Humidity sensor
static void humidity_reading() {

    //Read Humidity value
    humidity_val = hdc_1000_sensor.value(HDC_1000_SENSOR_TYPE_HUMIDITY);
    sprintf(str, "Humidity=%d.%02d %%Pa\n\r", humidity_val/100, humidity_val%100);
    tcp_socket_send_str(&socket, str);	//Reflect byte
    count ++;
    SENSORS_ACTIVATE(hdc_1000_sensor);
    if (count < 10) {
        ctimer_reset(&sensor_timer);
    } else {
        tcp_socket_send_str(&socket, "done\n\r");	//Reflect byte
        ctimer_stop(&sensor_timer);
    }
}

//Intialise Pressure sensor
static void pressure_reading() {

    //Read Pressure value
    pressure_val = bmp_280_sensor.value(BMP_280_SENSOR_TYPE_PRESS);
    sprintf(str, "Pressure=%d.%02d %%Pa\n\r", pressure_val/100, pressure_val%100);
    tcp_socket_send_str(&socket, str);	//Reflect byte
    count ++;
    SENSORS_ACTIVATE(bmp_280_sensor);
    if (count < 10) {
        ctimer_reset(&sensor_timer);
    } else {
        tcp_socket_send_str(&socket, "done\n\r");	//Reflect byte
        ctimer_stop(&sensor_timer);
    }
}

//make callback function toggle leds
static void leds_update(void *ptr) {
    // printf("callback %c, %d\n\r", leds_clr, led_on);
    if (led_on) {
        if (leds_clr == 'a') {
            //Toggle All LED
            leds_toggle(LEDS_ALL);
        } else if (leds_clr == 'g') {
            //Toggle Green LED
            leds_toggle(LEDS_GREEN);
        } else if (leds_clr == 'r') {
            //Toggle Red LED
            leds_toggle(LEDS_RED);
        }
    } else {
        leds_off(LEDS_ALL);
    }
    ctimer_reset(&timer1);
}

//callback function reset buzz
static void buz_reset(void *ptr) {
    //printf("reset callback state: %d, freq: %d\n\r", buz_state, FREQ);
    if (buz_state) {
        buzzer_start(FREQ);
    } else {
        buzzer_stop();
    }
    ctimer_stop(&timer);
}

//callback function update buzz for 5 second and reset buzz
static void buz_update(void *ptr) {
    //printf("update callback state: %d, freq: %d\n\r", buz_state, (FREQ+buz_freq));
    if (buz_state) {
        buzzer_start(FREQ + buz_freq);
    } else {
        buzzer_stop();
    }
    ctimer_stop(&timer);
    ctimer_set(&timer, 5*CLOCK_SECOND, buz_reset, NULL);
}


PROCESS(tcp_server_process, "TCP echo process");
AUTOSTART_PROCESSES(&tcp_server_process);


/*---------------------------------------------------------------------------*/
//Input data handler
static int input(struct tcp_socket *s, void *ptr, const uint8_t *inputptr, int inputdatalen) {

    printf("input %d bytes '%s'\n\r", inputdatalen, inputptr);

    char cmd = ((char*)inputptr)[0];
    printf("received line: %s %c\n\r", (char *)inputptr, cmd);
    if (cmd == 'a') {
        //Toggle All LEDs
        if (!led_on || cmd == leds_clr) {
            led_on = !led_on;
        } else if (led_on && cmd == leds_clr) {
            led_on = !led_on;
        }
        leds_clr='a';
    } else if (cmd == 'r') {
        if (!led_on || cmd == leds_clr) {
            led_on = !led_on;
        } else if (led_on && cmd == leds_clr) {
            led_on = !led_on;
        }
        //Toggle Red LED
        leds_clr='r';
    } else if (cmd == 'g') {
        if (!led_on || cmd == leds_clr) {
            led_on = !led_on;
        } else if (led_on && cmd == leds_clr) {
            led_on = !led_on;
        }
        //Toggle Green LED
        leds_clr='g';
    }

    if (cmd == 'b') {
        //          printf("freq: %d state %d\n\r", FREQ, buz_state);
        buz_state = !buz_state;
        ctimer_set(&timer, 0*CLOCK_SECOND, buz_reset, NULL);
    } else if (cmd == 'i' && buz_state) {
        buz_freq = 50;
        //          printf("freq: %d\n\r", (FREQ + buz_freq));
        ctimer_set(&timer, 0*CLOCK_SECOND, buz_update, NULL);
    } else if (cmd == 'd' && buz_state) {
        buz_freq = -50;
        //          printf("freq: %d\n\r", (FREQ + buz_freq));
        ctimer_set(&timer, 0*CLOCK_SECOND, buz_update, NULL);
    }

    if (cmd == 'h') {
        count = 0;
        ctimer_set(&sensor_timer, CLOCK_SECOND, humidity_reading, NULL);	//Callback timer for humidity sensor
        SENSORS_ACTIVATE(hdc_1000_sensor);
    }

    if (cmd == 'p') {
        count = 0;
        ctimer_set(&sensor_timer, CLOCK_SECOND, pressure_reading, NULL);	//Callback timer for humidity sensor
        SENSORS_ACTIVATE(bmp_280_sensor);
    }

    tcp_socket_send_str(&socket, (const char * )inputptr);	//Reflect byte

    //Clear buffer
    memset((void *)inputptr, 0, inputdatalen);
    return 0; // all data consumed
}

/*---------------------------------------------------------------------------*/
//Event handler
static void event(struct tcp_socket *s, void *ptr, tcp_socket_event_t ev) {
    printf("event %d\n", ev);
}

/*---------------------------------------------------------------------------*/
//TCP Server process
PROCESS_THREAD(tcp_server_process, ev, data) {

    PROCESS_BEGIN();

    //make a ctimer call the callback_fn every 500ms
    ctimer_set(&timer1, CLOCK_SECOND/2, leds_update, NULL);

    //Register TCP socket
    tcp_socket_register(&socket, NULL,
            inputbuf, sizeof(inputbuf),
            outputbuf, sizeof(outputbuf),
            input, event);
    tcp_socket_listen(&socket, SERVER_PORT);

    printf("Listening on %d\n", SERVER_PORT);

    while(1) {

        //Wait for event to occur
        PROCESS_PAUSE();
    }
    PROCESS_END();
}
/*---------------------------------------------------------------------------*/

