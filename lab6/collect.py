#!/usr/bin/python

import socket
import time
import datetime
import struct
import StringIO
from threading import Thread
import sys
import csv

UDP_REPLY_PORT = 47371 # node listens for reply packets on port 7005
UDP_TIMESYNC_PORT = 3000 # node listens for timesync packets on port 4003

isRunning = True

# create sockets at server start up
sock = socket.socket(socket.AF_INET6, socket.SOCK_DGRAM, 0)
# listen on UDP socket port UDP_TIMESYNC_PORT
recvSocket = socket.socket(socket.AF_INET6, socket.SOCK_DGRAM)
recvSocket.bind(("aaaa::1", UDP_REPLY_PORT))

reading_bright = []
ad="aaaa::212:4b00:b00:6403"
reading_dark = []
ad2="aaaa::212:4b00:f0e:7585"

def udpListenThread():
  recvSocket.settimeout(0.5)
  while isRunning:

    try:
      data, addr = recvSocket.recvfrom( 1024 )
      if len(reading_bright) <= 600 and addr[0] == ad:
        light = (struct.unpack("I", data[0:4]))[0]
        reading_bright.append(light);
        print "%d Reply from: %s Light=%d.%02d" % (len(reading_bright), addr[0],light/100, light%100)
      if len(reading_dark) <= 600 and addr[0] == ad2:
        light = (struct.unpack("I", data[0:4]))[0]
        reading_dark.append(light);
        print "%d Reply from: %s Light=%d.%02d" % (len(reading_bright), addr[0],light/100, light%100)
    except socket.timeout:
      pass

def udpSendThread():

  while isRunning:

    # send UDP packet to nodes
    # change the IP Address with your sensorTag accordingly. (Addresses are shown on browser
    # from the border router)
    # you may start with one sensortag.
    if len(reading_bright) == 0:
      s = 'light'
      sock.sendto(s, (ad, UDP_TIMESYNC_PORT))
      print "%d Send to: %s, %s" % (len(reading_bright), s, ad)
    elif len(reading_bright) == 600:
      s = 'stop'
      sock.sendto(s, (ad, UDP_TIMESYNC_PORT))
      with open('record-bright.csv', 'wb') as f:
          writer = csv.writer(f, delimiter=' ')
          print(reading_bright)
          writer.writerow(reading_bright)
      reading_bright.append(0)
    if len(reading_dark) == 0:
      s = 'light'
      sock.sendto(s, (ad2, UDP_TIMESYNC_PORT))
      print "%d Send to: %s, %s" % (len(reading_dark), s, ad)
    elif len(reading_dark) == 600:
      s = 'stop'
      sock.sendto(s, (ad2, UDP_TIMESYNC_PORT))

      with open('record-dark.csv', 'wb') as f:
          writer = csv.writer(f, delimiter=' ')
          print(reading_dark)
          writer.writerow(reading_dark)

      print "%d Send to: %s, %s" % (len(reading_bright), s, ad)
    # sleep for some seconds
    # the frequency of sending the sych timestamps packet is very important
    # you will see how this affect the sych accuracy in your experiment
    time.sleep(.1)


# start UDP listener as a thread
t1 = Thread(target=udpListenThread)
t1.start()
print "Listening for incoming packets on UDP port", UDP_REPLY_PORT

time.sleep(1)

# start UDP timesync sender as a thread
t2 = Thread(target=udpSendThread)
t2.start()

print "Sending timesync packets on UDP port", UDP_TIMESYNC_PORT
print "Exit application by pressing (CTRL-C)"

try:
  while True:
    # wait for application to finish (ctrl-c)
    time.sleep(1)
except KeyboardInterrupt:
  print "Keyboard interrupt received. Exiting."
  isRunning = False




