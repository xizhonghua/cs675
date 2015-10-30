#!/usr/bin/env python

import socket
import time
import sys
from util import now

UDP_IP = "127.0.0.1"
UDP_PORT = 7778

if len(sys.argv) > 1:
  UDP_IP = sys.argv[1]

server = (UDP_IP, UDP_PORT)

print 'Server =', server

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

seq = 0

while True:
  seq += 1
  message = str(seq) + " " + now()
  sock.sendto(message, server)
  resp, addr = sock.recvfrom(1024)
  print "received message =", resp, "server addr =", addr

  items = resp.split()

  resp_seq = int(items[0])
  t0 = time.time()
  t1 = float(items[3])
  t2 = float(items[2])
  t3 = float(items[1])

  RTT = (t2 - t3) + (t0 - t1)
  theta = ((t2 - t3) - (t0 - t1)) / 2

  print RTT, theta

  time.sleep(2)
