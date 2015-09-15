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

  def log_message(self, format, *args):
    pass

  def get_par(self, qs, key, default_val=None):
    if key in qs:
      return qs[key][0]
    return default_val

  def do_GET(self):

    node = self.server.node

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

    print '[REST][Server] Request =', self.path

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
        new_zone, new_files, new_neighbors = node.split(peer, source)
        rsp = {
            'new_zone': new_zone.__dict__,
            'new_files': new_files,
            'new_neighbors': new_neighbors}
      else:
        # forward the message
        rsp = node.get(closest_nb, self.path)
    else:
      rsp = {"status": "error", "error_message": "unknown command"}

    # append current node's address to route list
    if 'route' in rsp:
      rsp['route'].append(node.get_address())
    else:
      rsp['route'] = [node.get_address()]

    message = json.dumps(rsp)
    print '[REST][Server] Response =', message

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
    d['neighbors'] = self.neighbors
    d['bootstrap'] = self.bootstrap
    return str(d)

  def get_address(self):
    return self.ip + ':' + str(self.port)

  def as_neighbor(self):
    return {'id': self.id, 'address': self.get_address(),
            'zone': self.zone.__dict__}

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

  def call(self, address, path, kv=None):
    if kv is not None:
      qs = urllib.urlencode(kv)
      path = path + '?' + qs

    url = "http://%s%s" % (address, path)
    response = urllib2.urlopen(url)
    data = json.load(response)

    print '[REST][Client] Request =', url
    print '[REST][Client] Response =', data

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
    (new_zone, new_keywords, new_neighbors)
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

    # Update neighbor info

    self.neighbors[source] = {
        'id': peer,
        'address': source,
        'zone': new_zone.__dict__}

    new_neighbors = {}

    new_neighbors[self.get_address()] = self.as_neighbor()

    # rsp = {'new_zone': new_zone.__dict__}
    # TODO(zxi)
    # notify neighbors

    print 'Split!'
    self.view()

    return new_zone, new_files, new_neighbors

  def buildQuestParameters(self, p=None, keyword=None, content=None):
    pars = {
        'peer': self.id,
        'source': self.get_address()}
    if p is not None:
      pars['x'] = p.x
      pars['y'] = p.y
    if keyword is not None:
      pars['keyword'] = keyword
    if content is not None:
      pars['content'] = content
    return pars

  def joinCAN(self):

    self.server = ThreadedHTTPServer((self.ip, self.port), Handler, self)
    self.server.start()

    # Wait the HTTPServer start
    while not self.server.started:
      time.sleep(0.2)

    print 'Joining CAN...'
    p = random_point()
    print 'Random point picked =', p

    if self.bootstrap is None:
      pass
    else:
      pars = self.buildQuestParameters(p)
      rsp = self.call(self.bootstrap, '/join', pars)
      self.zone = rsp['new_zone']
      self.files = rsp['new_files']
      self.neighbors = rsp['new_neighbors']

    print 'Joined!'
    self.view()

  '''
  Insert the keyword and content into CAN
  '''

  def insertFile(self, keyword, content):
    p = hash(keyword)
    if self.zone.contains(p):
      self.addFile(keyword, content)
      self.view()
    else:
      address = self.get_closest_neighbor(p)
      pars = self.buildQuestParameters(p, keyword=keyword, content=content)
      rsp = self.call(address, '/insert', pars)
    pass

  def addFile(self, keyword, content):
    if keyword in self.files:
      self.files[keyword].append(content)
    else:
      self.files[keyword] = [content]

  def stop(self):
    if self.server is not None:
      self.server.stop()

  def leaveCAN(self):
    pass

  def run(self):
    print 'Node started'
    self.joinCAN()

    while(True):
      items = raw_input("").split(' ')
      cmd = items[0]
      # print cmd
      if cmd == "join":
        # self.joinCAN()
        pass
      elif cmd == "insert":
        if len(items) < 3:
          print "insert keyword content"
        else:
          self.insertFile(items[1], items[2])
      elif cmd == "view":
        self.view()
      elif cmd == "leave":
        pass
      elif cmd == "exit":
        self.stop()
        break
      elif cmd == "test":
        print self.get(self.get_address(), '/', {'k1': 'v1', 'k2': 'v2', 'foo': 'bar'})
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
