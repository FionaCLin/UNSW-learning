/**
 * \file
 *         A simple Contiki application showing serial interfacing
 * \author
 *         mds
 */
#include "contiki.h"
#include "buzzer.h"
#include <stdio.h> /* For printf() */
#include <string.h>
#include "dev/serial-line.h"
#include "dev/cc26xx-uart.h"
#include "sys/ctimer.h"

static struct ctimer timer;

static const int FREQ = 1000;
static int  buz_state = 0;
static int  buz_freq = 0;

//callback function reset buzz
static void buz_reset(void *ptr) {
    printf("reset callback state: %d, freq: %d\n\r", buz_state, FREQ);
    if (buz_state) {
        buzzer_start(FREQ);
    } else {
        buzzer_stop();
    }
    ctimer_stop(&timer);
}

//callback function update buzz for 5 second and reset buzz
static void buz_update(void *ptr) {
    printf("update callback state: %d, freq: %d\n\r", buz_state, (FREQ+buz_freq));
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
            printf("received line: %s\n\r", (char *)data);
            char cmd = ((char *)data)[0];
            if (cmd == 'b') {
                printf("freq: %d state %d\n\r", FREQ, buz_state);
                buz_state = !buz_state;
                ctimer_set(&timer, 0*CLOCK_SECOND, buz_reset, NULL);
            } else if (cmd == 'i' && buz_state) {
                buz_freq = 50;
                printf("freq: %d\n\r", (FREQ + buz_freq));
                ctimer_set(&timer, 0*CLOCK_SECOND, buz_update, NULL);
            } else if (cmd == 'd' && buz_state) {
                buz_freq = -50;
                printf("freq: %d\n\r", (FREQ + buz_freq));
                ctimer_set(&timer, 0*CLOCK_SECOND, buz_update, NULL);
            }
        }
    }
    PROCESS_END();
}
/*---------------------------------------------------------------------------*/
