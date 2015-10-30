#!/usr/bin/env python

import socket
import time
import sys
from util import now
from datetime import datetime, timedelta

SLEEP_TIME = 10  # sec
TIME_OUT = 1.0   # sec
MAX_ESTIMATIONS = 8

UDP_IP = "127.0.0.1"
UDP_PORT = 7778

if len(sys.argv) > 1:
  UDP_IP = sys.argv[1]

server = (UDP_IP, UDP_PORT)

print 'Server =', server

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

seq = 0

estimations = []

prev_t0 = 0
prev_t1 = 0

sent = 0
received = 0

while True:
  seq += 1
  message = str(seq) + " " + now()
  sock.settimeout(None)
  sock.sendto(message, server)
  sent += 1
  sock.settimeout(TIME_OUT)

  try:
    while True:
      resp, addr = sock.recvfrom(1024)
      if not resp:
        break

      items = resp.split()

      resp_seq = int(items[0])
      t0 = time.time()
      t1 = float(items[3])
      t2 = float(items[2])
      t3 = float(items[1])

      RTT = (t2 - t3) + (t0 - t1)

      theta = ((t2 - t3) - (t0 - t1)) / 2

      if RTT < TIME_OUT * 2:

        received += 1

        if len(estimations) == MAX_ESTIMATIONS:
          estimations.pop(0)

        estimations.append((RTT, theta))

        MIN_RTT = 1e3
        BEST_THETA = 0

        for (r, t) in estimations:
          if r < MIN_RTT:
            MIN_RTT = r
            BEST_THETA = t

        corrected_time = datetime.now() + timedelta(0, BEST_THETA)

        clock_draft_rate = 0.0

        if prev_t0 > 0:
          clock_draft_rate = ((t0 - prev_t0) - (t1 - prev_t1)
                              ) / (t1 - prev_t1) * 1e6

        prev_t1 = t1
        prev_t0 = t0

        # valid data
        print datetime.now(), "MSG =", resp, "server addr =", addr, 'RTT =', RTT, 'theta =', theta, 'CT =', corrected_time, 'BEST_THETA =', BEST_THETA, 'CDR =', clock_draft_rate, 'sent =', sent, 'received =', received
      else:
        print datetime.now(), 'Network timeout'

  except socket.timeout as e:
    pass

  time.sleep(SLEEP_TIME)
