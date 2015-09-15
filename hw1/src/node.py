#!/usr/bin/env python
from BaseHTTPServer import HTTPServer, BaseHTTPRequestHandler
from SocketServer import ThreadingMixIn
import threading
import urllib2
import urllib
import urlparse
import sys
import time
import json
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

  def get_par(self, qs, key, default_val=None):
    if key in qs:
      return qs[key][0]
    return default_val

  def do_GET(self):

    node = self.server.node

    print 'node.id = ', node.id

    print 'request.path = ', self.path

    url = urlparse.urlparse(self.path)
    qs = urlparse.parse_qs(url.query)

    message = threading.currentThread().getName()

    peer = self.get_par(qs, 'peer')
    source = self.get_par(qs, 'source')
    keyword = self.get_par(qs, 'keyword')

    x = int(self.get_par(qs, 'x', -1))
    y = int(self.get_par(qs, 'y', -1))
    p = Point(x, y)

    in_zone = node.zone.contains(p)
    closest_nb = node.get_closest_neighbor(p)

    rsp = {}

    if url.path == "/":
      rsp = {"status": "ok", "data": "hello_world"}
    elif url.path == "/search":
      rsp = node.search(keyword)
    elif url.path == "/view":
      rsp = node.view()
    elif url.path == "/insert":
      pass
    elif url.path == "/join":
      if in_zone:
        new_zone, new_files = node.split(peer, source)
        rsp = {'new_zone': new_zone.__dict__, 'new_files': new_files}
      else:
        # forward the message
        rsp = node.get(closest_nb, self.path)
    else:
      rsp = {"status": "error", "error_message": "unknown command"}

    # append current node's address to route list
    if 'route' in rsp:
      rsp['route'].append(node._get_address())
    else:
      rsp['route'] = [node._get_address()]

    message = json.dumps(rsp)
    print '[response] =', message

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

    # key
    self.files = {}

    # Neighbors
    #   key : address
    #   value : (id, address, zone)
    self.neighbors = {}

    # Assuming current node initially own the entire space
    self.zone = Zone(0, 0, ZONE_MAX_WIDTH, ZONE_MAX_HEIGHT)

    # A CAN node in the system [(ip, port)]
    self.bootstrap = bootstrap

  def __str__(self):
    d = {}
    d['id'] = self.id
    d['ip'] = self.ip
    d['port'] = self.port
    d['zone'] = str(self.zone)
    d['files'] = str(self.files)
    d['bootstrap'] = self.bootstrap
    return str(d)

  def _get_address(self):
    return self.ip + ':' + str(self.port)

  '''
  Get the closest neighbors for a given target point
  '''

  def get_closest_neighbor(self, point):
    min_dist = 65535
    closest_nb = None

    for nb in self.neighbors:
      nb_zone = self.neighbors[nb]
      dist = nb_zone.dist(point)
      if dist < min_dist:
        closest_nb = nb
        min_dist = dist

    return closest_nb

  '''
  Call REST API at http://address/path?k1=v1&k2=v2... and get result
  '''

  def get(self, address, path, kv=None):
    if kv is not None:
      qs = urllib.urlencode(kv)
      path = path + '?' + qs

    response = urllib2.urlopen(
        "http://%s%s" % (address, path))
    data = json.load(response)
    return data

  def search(self, keyword):
    print 'query id=%s, keyword=%s' % (self.id, keyword)

  def view(self):
    print '[View]', self

  '''
  Split the zone owned by current node
  And notify the all the neighbors
  peer:
    peer id
  source:
    peer address in ip:port format
  return:
    (new_zone, new_keywords)
  '''

  def split(self, peer, source):

    org_zone = self.zone.clone()

    # Split the zone
    new_zone = self.zone.split()

    # Split the files
    new_files = {}

    # Add files to new zone
    for keyword in self.files:
      if new_zone.contains(hash(keyword)):
        new_files[keyword] = self.files[keyword]

    # Remove files from current zone
    for keyword in new_files:
      del self.files[keyword]

    # rsp = {'new_zone': new_zone.__dict__}
    # TODO(zxi)
    # notify neighbors

    return new_zone, new_files

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
      pars = {
          'x': p.x,
          'y': p.y,
          'peer': self.id,
          'source': self._get_address()}
      # Call REST API
      rsp = self.get(self.bootstrap, '/join', pars)
      print rsp

    print 'Joined!'
    self.view()

  def stop(self):
    if self.server is not None:
      self.server.stop()

  def leaveCAN(self):
    pass

  def run(self):
    print 'Node started'
    self.joinCAN()

    while(True):
      var = raw_input("")
      print var
      if var == "join":
        # self.joinCAN()
        pass
      elif var == "view":
        self.view()
      elif var == "leave":
        pass
      elif var == "exit":
        self.stop()
        break
      elif var == "test":
        print self.get(self._get_address(), '/', {'k1': 'v1', 'k2': 'v2', 'foo': 'bar'})
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
