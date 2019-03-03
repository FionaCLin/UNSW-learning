/**
 * \file
 *         A simple Contiki application showing serial interfacing
 * \author
 *         mds
 */
#include "contiki.h"
#include "dev/serial-line.h"
#include "buzzer.h"
#include <stdio.h> /* For printf() */
#include "dev/cc26xx-uart.h"
#include "ieee-addr.h"

uint8_t addr[8];

/*---------------------------------------------------------------------------*/
PROCESS(test_serial, "Serial line test process");
AUTOSTART_PROCESSES(&test_serial);
/*---------------------------------------------------------------------------*/
//Serial Interface
PROCESS_THREAD(test_serial, ev, data) {

    PROCESS_BEGIN();

    //Initalise UART in serial driver
    cc26xx_uart_set_input(serial_line_input_byte);

    //mac addr 8byte or 6?
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
