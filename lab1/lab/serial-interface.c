/**
 * \file
 *         A simple Contiki application showing serial interfacing
 * \author
 *         mds
 */
#include "contiki.h"
#include "buzzer.h"
#include <stdio.h> /* For printf() */
#include "dev/serial-line.h"
#include "dev/cc26xx-uart.h"
#include "dev/leds.h"
#include "sys/ctimer.h"
#include "ieee-addr.h"

uint8_t addr[8];

static struct ctimer timer;

static char leds_clr='a';
static int led_on=0;

static const int FREQ = 1000;
static int  buz_state = 0;
static int  buz_freq = 0;


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
    ctimer_reset(&timer);
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


/*---------------------------------------------------------------------------*/
PROCESS(test_serial, "Serial line test process");
AUTOSTART_PROCESSES(&test_serial);
/*---------------------------------------------------------------------------*/
//Serial Interface
PROCESS_THREAD(test_serial, ev, data) {

    PROCESS_BEGIN();

    //Initalise UART in serial driver
    cc26xx_uart_set_input(serial_line_input_byte);

    //make a ctimer call the callback_fn every 500ms
    ctimer_set(&timer, CLOCK_SECOND/2, leds_update, NULL);

    while(1) {

        PROCESS_YIELD();	//Let other threads run

        //Wait for event triggered by serial input

        //******************************************
        //NOTE: MUST HOLD CTRL and then press ENTER
        //at the end of typing for the serial driver
        //to work. Serial driver expects 0x0A as
        //last character, to tigger the event.
        //******************************************
        if(ev == serial_line_event_message) {
            char cmd = ((char*)data)[0];
            printf("received line: %s %c\n\r", (char *)data, cmd);
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

            if (((char*)data)[0] == 'n') {
                ieee_addr_cpy_to(addr, 8);
                printf("IEEE(MAC): %02x:%02x:%02x:%02x:%02x:%02x:%02x:%02x\n\r", addr[0],
                        addr[1], addr[2], addr[3], addr[4], addr[5], addr[6], addr[7]);
            }

        }
    }
    PROCESS_END();
}
/*---------------------------------------------------------------------------*/
