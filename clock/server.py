#!/usr/bin/env python

import socket
from util import now

UDP_IP = "127.0.0.1"
UDP_PORT = 7778

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

sock.bind(("", UDP_PORT))

print 'Time Server listened at port %s' % UDP_PORT

while True:
  data, addr = sock.recvfrom(1024)
  received_time = now()
  print "received message =", data, " addr =", addr
  if data:
    response_time = now()
    response = data + " " + received_time + " " + response_time
    sock.sendto(response, addr)
