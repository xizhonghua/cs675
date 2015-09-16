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

  def handle_view(self):
    self.rsp = self.node.view()

  def handle_join(self):
    if not self.in_zone:
      return

    new_zone, new_files, new_neighbors = self.node.split(
        self.peer, self.source)

    self.rsp = {
        'new_zone': new_zone.__dict__,
        'new_files': new_files,
        'new_neighbors': new_neighbors}

  def handle_insert(self):
    if not self.in_zone:
      return

    self.node.add_file(self.keyword, self.content)
    self.rsp = {'result': 'success',
                'keyword': self.keyword,
                'content': self.content
                }

  def handle_search(self):
    if not self.in_zone:
      return

    files = self.node.files[
        self.keyword] if self.keyword in self.node.files else {}

    self.rsp = {'result': 'success',
                'keyword': self.keyword,
                'contents': files}
    return

  def handle_view(self):
    self.rsp = self.node.view()
    return

  def handle_add_neighbor(self):
    neighbor = json.load(self.neighbor)
    is_success = self.node.add_neighbor(neighbor)
    self.rsp = {'success': is_success}
    return

  def handle_remove_neightbor(self):
    neighbor = json.load(self.neighbor)
    is_success = self.node.remove_neighbor(neighbor)
    self.rsp = {'success': is_success}
    return

  def do_GET(self):

    node = self.node = self.server.node

    url = urlparse.urlparse(self.path)
    qs = urlparse.parse_qs(url.query)

    message = threading.currentThread().getName()

    self.peer = self.get_par(qs, 'peer')
    self.source = self.get_par(qs, 'source')
    self.keyword = self.get_par(qs, 'keyword')
    self.content = self.get_par(qs, 'content')
    self.neighbor = self.get_par(qs, 'neighbor')

    self.x = int(self.get_par(qs, 'x', -1))
    self.y = int(self.get_par(qs, 'y', -1))
    self.p = Point(self.x, self.y)

    self.in_zone = node.zone.contains(self.p)
    self.closest_nb = node.get_closest_neighbor(self.p)

    # if p is not in zone forward the message to the closest neighbor
    self.is_forward_message = True

    self.rsp = {}

    print '[REST][Server] Request =', self.path

    if url.path == "/":
      self.is_forward_message = False
      rsp = {"status": "ok", "data": "hello_world"}
    elif url.path == "/search":
      self.handle_search()
    elif url.path == "/view":
      self.handle_view()
    elif url.path == "/insert":
      self.handle_insert()
    elif url.path == "/join":
      self.handle_join()
    elif url.path == "/merge":
      self.handle_merge()
    elif url.path == "/add-neighbor":
      self.handle_add_neighbor()
    elif url.path == "/remove-neighbor":
      self.handle_remove_neighbor()
    else:
      # Unknown command, do not forward message
      self.is_forward_message = False
      self.rsp = {"status": "error", "error_message": "unknown command"}

    if not self.in_zone and self.is_forward_message:
      # forward the message
      self.rsp = node.call(self.closest_nb, self.path)

    # append current node's address to route list
    if 'route' in self.rsp:
      self.rsp['route'].append(node.get_address())
    else:
      self.rsp['route'] = [node.get_address()]

    message = json.dumps(self.rsp)
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
    #   value : {id, address, zone}
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
    d['neighbors'] = str(self.neighbors)
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
      nb_zone = self.neighbors[nb]['zone']
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

    new_node = {
        'id': peer,
        'address': source,
        'zone': new_zone
    }

    new_neighbors = {}

    new_neighbors[self.get_address()] = self.as_neighbor()

    # notify neighbors

    for address in self.neighbors:
      neighbor = self.neighbors[address]
      if neighbor['zone'].is_neightbor(new_zone):
        new_neighbors[address] = neighbor

        pars = self.build_request_paras(neighbor=new_node)
        self.call(address, "/add-neighbor", pars)

    self.neighbors[source] = new_node

    self.view()

    return new_zone, new_files, new_neighbors

  def build_request_paras(
          self, p=None, keyword=None, content=None, contents=None, neighbor=None):
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
    if neighbor is not None:
      pars['neighbor'] = neighbor
    if contents is not None:
      pars['contents'] = contents
    return pars

  def join_CAN(self):

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
      pars = self.build_request_paras(p)
      rsp = self.call(self.bootstrap, '/join', pars)

      # update node info
      self.zone.setFromDict(rsp['new_zone'])
      self.files = rsp['new_files']

      new_neighbors = rsp['new_neighbors']

      for address in new_neighbors:
        self.add_neighbor(new_neighbors[address])

    print 'Joined!'
    self.view()

  def add_neighbor(self, neighbor):
    nb_address = neighbor['address']
    if nb_address in self.neighbors:
      return False

    neighbor['zone'] = Zone().setFromDict(neighbor['zone'])
    self.neighbors[nb_address] = neighbor
    return True

  def remove_neighbor(self, neighbor):
    nb_address = neighbor['address']
    if nb_address in self.neighbors:
      del self.neighbors[nb_address]
      return True
    return False

  '''
  Search for a keyword
  '''

  def search_file(self, keyword):
    p = hash(keyword)
    contents = []
    if self.zone.contains(p) and keyword in self.files:
      contents = self.files[keyword]
    else:
      address = self.get_closest_neighbor(p)
      pars = self.build_request_paras(p, keyword=keyword)
      rsp = self.call(address, '/search', pars)
      contents = rsp['contents']  # TODO(zxi) fix me
    print '[Search] keyword = "%s" contents = "%s"' % (keyword, contents)

  '''
  Insert the keyword and content into CAN
  '''

  def insert_file(self, keyword, content):
    p = hash(keyword)
    if self.zone.contains(p):
      self.add_file(keyword, content)
      self.view()
    else:
      address = self.get_closest_neighbor(p)
      pars = self.build_request_paras(p, keyword=keyword, content=content)
      rsp = self.call(address, '/insert', pars)
    pass

  def add_file(self, keyword, content):
    if keyword in self.files:
      self.files[keyword].append(content)
    else:
      self.files[keyword] = [content]

  def stop(self):
    if self.server is not None:
      self.server.stop()

  '''
  Leave the CAN.
  1) find the mergeable neighbor, and migrate zone/files to that node
  2) notify all the neighbors
  '''

  def leave_CAN(self):
    mergeable_nb = None

    # merge zone, migrate files
    for nb in self.neighbors:
      if nb['zone'].is_mergeable(self.zone):
        mergeable_nb = nb
        pars = self.build_request_paras(
            contents=self.files)
        self.call(nb['address'], '/merge')
        break

    # notify all other neighbors
    for nb in self.neighbors:
      if nb == mergeable_nb:
        continue
      pars = self.build_request_paras(neighbor=mergeable_nb)
      self.call(nb['address'], '/remove-neighbor')

  '''
  Merge zones.
  source:
    address of node to merge
  contents:
    contents to add
  '''

  def merge(self, source, contents):
    # should be a neighbor
    nb = self.neighbors[source]

    # merge zones
    self.zone.merge(nb['zone'])

    # merge files
    for keyword in contents:
      self.files[keyword] = contents[keyword]

  def invoke_cmd(self, cmd, args):
    # print cmd
    if cmd == "join":
      # self.join_CAN()
      pass
    elif cmd == "insert":
      if len(args) < 2:
        print "insert keyword content"
      else:
        self.insert_file(args[0], args[1])
    elif cmd == "search":
      if len(args) == 0:
        print "search keyword"
      else:
        self.search_file(args[0])
    elif cmd == "view":
      self.view()
    elif cmd == "leave":
      self.leave_CAN()
    elif cmd == "exit":
      self.stop()
      return False
    elif cmd == "test":
      print self.call(self.get_address(), '/', {'k1': 'v1', 'k2': 'v2', 'foo': 'bar'})
    else:
      print '[Error] Unknown command: %s' % cmd

    return True

  def run(self):
    print 'Node started'
    self.join_CAN()

    while(True):
      args = raw_input("").split(' ')
      cmd = args[0]
      args.pop(0)
      if not self.invoke_cmd(cmd, args):
        break

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
