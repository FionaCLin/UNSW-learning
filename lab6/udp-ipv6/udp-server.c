/*
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of the Contiki operating system.
 *
 */

#include "contiki.h"
#include "contiki-lib.h"
#include "contiki-net.h"
#include "sys/clock.h"
#include "board-peripherals.h"
#include "dev/leds.h"
#include "sys/ctimer.h"

#include <string.h>
#include <stdio.h>

#define DEBUG DEBUG_PRINT
#include "net/ip/uip-debug.h"

#define UIP_IP_BUF   ((struct uip_udpip_hdr *)&uip_buf[UIP_LLH_LEN])

#define MAX_PAYLOAD_LEN 120

static struct uip_udp_conn *server_conn;

static struct ctimer sensor_timer;

static int light_val;
static int count = 0;
static union uip_ip6addr_t srcipaddr;

//Intialise light sensor
static void light_reading(void * ptr) {

    //Read light value
    light_val = opt_3001_sensor.value(0);
    printf("count %d light=%d.%02d %%lux\n\r", count, light_val/100, light_val%100);

    PRINT6ADDR(&srcipaddr);
    PRINTF("\n\r");
    uip_udp_packet_sendto(server_conn, &light_val, 4 , &srcipaddr, UIP_HTONS(47371));
    /* Restore server connection to allow data from any node */
//    memset(&server_conn->ripaddr, 0, sizeof(server_conn->ripaddr));
    count ++;
    SENSORS_ACTIVATE(opt_3001_sensor);
    ctimer_reset(&sensor_timer);
}

PROCESS(udp_server_process, "UDP server process");
AUTOSTART_PROCESSES(&resolv_process,&udp_server_process);
/*---------------------------------------------------------------------------*/
    static void
tcpip_handler(void)
{

    if(uip_newdata()) {
        // python udp server has packed the uint so use int pointer instead char *
        ((char *)uip_appdata)[uip_datalen()] = 0;
        // update the response address
        // debug print
        printf("Server received: '%s' from ", (char *)uip_appdata);
        PRINT6ADDR(&UIP_IP_BUF->srcipaddr);
        PRINTF("\n\r");



        if (strcmp(uip_appdata, "light") == 0 && count==0) {
            printf("Start collect: \n\r");
            srcipaddr = UIP_IP_BUF->srcipaddr;
            ctimer_set(&sensor_timer, CLOCK_SECOND/5, light_reading, NULL);
            SENSORS_ACTIVATE(opt_3001_sensor);
        } else if (strcmp(uip_appdata, "stop") == 0 && count!=0) {
            printf("Stop collect: \n\r");
            count = 0;
            ctimer_stop(&sensor_timer);
        }
    }
}
/*---------------------------------------------------------------------------*/
    static void
print_local_addresses(void)
{
    int i;
    uint8_t state;

    PRINTF("Server IPv6 addresses: ");
    for(i = 0; i < UIP_DS6_ADDR_NB; i++) {
        state = uip_ds6_if.addr_list[i].state;
        if(uip_ds6_if.addr_list[i].isused &&
                (state == ADDR_TENTATIVE || state == ADDR_PREFERRED)) {
            PRINT6ADDR(&uip_ds6_if.addr_list[i].ipaddr);
            PRINTF("\n\r");
        }
    }
}
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(udp_server_process, ev, data)
{
#if UIP_CONF_ROUTER
    uip_ipaddr_t ipaddr;
#endif /* UIP_CONF_ROUTER */
    PROCESS_BEGIN();
    PRINTF("UDP server started\n\r");

#if RESOLV_CONF_SUPPORTS_MDNS
    resolv_set_hostname("contiki-udp-server");
#endif

#if UIP_CONF_ROUTER
    uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
    uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
    uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF);
#endif /* UIP_CONF_ROUTER */

    print_local_addresses();

    //Create UDP socket and bind to port 3000

    //## need to set this UIP_HTONS(0)
    server_conn = udp_new(NULL, UIP_HTONS(0), NULL);
    udp_bind(server_conn, UIP_HTONS(3000));
    PRINTF("Server listening on UDP port %u\n", UIP_HTONS(server_conn->lport));
    while(1) {
        PROCESS_YIELD();

        //Wait for tcipip event to occur
        if(ev == tcpip_event) {
            tcpip_handler();
        }
    }

    PROCESS_END();
}
/*---------------------------------------------------------------------------*/
