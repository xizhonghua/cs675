#!/usr/bin/env python
from BaseHTTPServer import HTTPServer, BaseHTTPRequestHandler
from SocketServer import ThreadingMixIn
import threading
import urllib2
import urlparse
import sys
import time
from util import *
from zone import *


class ThreadedHTTPServer(threading.Thread, ThreadingMixIn, HTTPServer):

  def __init__(self, address, Handler, node):
    HTTPServer.__init__(self, address, Handler)
    threading.Thread.__init__(self)
    self.should_stop = False
    self.started = False
    self.address = address
    self.node = node

  def get_index(self):
    return urllib2.urlopen("http://%s:%s" % self.address).read()

  def stop(self):
    print 'HTTPServer stopped'
    self.should_stop = True
    self.get_index()

  def run(self):
    print 'HTTPServer started on ', self.address
    self.started = True
    while not self.should_stop:
      self.handle_request()


class Handler(BaseHTTPRequestHandler):

  def get_str(self, qs, key, default_val=None):
    if key in qs:
      return qs[key][0]
    return default_val

  def do_GET(self):

    node = self.server.node

    print 'node.id = ', node.id

    url = urlparse.urlparse(self.path)
    qs = urlparse.parse_qs(url.query)

    print 'path = ', url.path
    print 'querystring = ', qs

    message = threading.currentThread().getName()

    peer = self.get_str(qs, 'peer')
    keyword = self.get_str(qs, 'keyword')

    x = int(self.get_str(qs, 'x', -1))
    y = int(self.get_str(qs, 'y', -1))
    p = Point(x, y)

    in_zone = node.zone.contains(p)

    print 'peer=', peer
    print 'keyword=', keyword

    if url.path == "/":
      message = '{"status": "ok", "data": "hello_world"}'
    elif url.path == "/search":
      json = node.search(keyword)
    elif url.path == "/view":
      json = node.view()
    elif url.path == "/insert":
      pass
    elif url.path == "/join":
      if in_zone:
        new_zone = node.zone
    else:
      message = '{"status": "error", "error_message": "unknown command"}'

    self.send_response(200)
    self.end_headers()
    self.wfile.write(message)
    self.wfile.write('\n')
    return


class Node(threading.Thread):

  def __init__(self, id, ip, port, bootstrap):
    threading.Thread.__init__(self)
    self.id = id
    self.ip = ip
    self.port = port
    self.address = (ip, port)
    self.daemon = True
    self.server = None

    # Assuming current node own the entire space
    self.zone = Zone(0, 0, ZONE_MAX_WIDTH, ZONE_MAX_HEIGHT)

    # A CAN node in the system [(ip, port)]
    self.bootstrap = bootstrap

  def __str__(self):
    d = {}
    d['id'] = self.id
    d['ip'] = self.ip
    d['port'] = self.port
    d['zone'] = str(self.zone)
    return str(d)

  '''
  Call REST API at http://address/path and get result
  '''

  def get(self, address, path):
    return urllib2.urlopen(
        "http://%s:%s%s" % (address[0], address[1], path)).read()

  def search(self, keyword):
    print 'query id=%s, keyword=%s' % (self.id, keyword)

  def view(self):
    print '[View]', self

  def joinCAN(self):
    print 'Joining CAN...'
    p = random_point()
    print 'Random point picked =', p

    self.server = ThreadedHTTPServer((self.ip, self.port), Handler, self)
    self.server.start()

    # Wait the HTTPServer start
    while not self.server.started:
      time.sleep(0.2)

    if self.bootstrap is None:
      pass
    else:
      # TODO(zxi)
      pass

    print 'Joined!'
    self.view()

  def stop(self):
    if self.server is not None:
      self.server.stop()

  def leaveCAN(self):
    pass

  def run(self):
    print 'Node started'
    while(True):
      var = raw_input("")
      print var
      if var == "join":
        self.joinCAN()
      elif var == "view":
        self.view()
      elif var == "leave":
        pass
      elif var == "exit":
        self.stop()
        break
      elif var == "test":
        print self.get(self.address, '/')
    print 'Node stopped'


if __name__ == '__main__':
  if len(sys.argv) <= 2:
    print 'Usage: %s peer_id port [bootstrap]' % sys.argv[0]
  else:
    peer_id = sys.argv[1]
    port = int(sys.argv[2])

    bootstrap = None
    if len(sys.argv) > 3:
      bootstrap = sys.argv[3]

    node = Node(peer_id, '127.0.0.1', port, bootstrap)
    node.start()
    node.join()
