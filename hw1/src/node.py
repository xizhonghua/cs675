#!/usr/bin/env python
from BaseHTTPServer import HTTPServer, BaseHTTPRequestHandler
from SocketServer import ThreadingMixIn
import threading
import urllib2
import urlparse
from util import hash{
    "tab_size": 4,
    "translate_tabs_to_spaces": false
}


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

    print 'peer=', peer
    print 'keyword=', keyword

    if url.path == "/search":
      json = node.search(keyword)
    elif url.path == "/view":
      json = node.view()
    elif url.path == "/insert":
      pass
    else:
      message = '{"status": "error", "error_message": "unknown command"}'

    self.send_response(200)
    self.end_headers()
    self.wfile.write(message)
    self.wfile.write('\n')
    return


class Node():

  def __init__(self, id, ip, port, Hanlder):
    self.id = id
    self.ip = ip
    self.port = port
    self.server = ThreadedHTTPServer((ip, port), Hanlder, self)
    self.daemon = True

  def search(self, keyword):
    print 'query id=%s, keyword=%s' % (self.id, keyword)
    pass

  def view(self):
    print 'view id=', self.id
    pass

  def joinCAN(self):
    print 'Joining...'
    self.server.start()

  def leaveCAN(self):
    pass

  def run(self):
    print 'Node started'
    while(True):
      var = raw_input("")
      print var
      if var == "join":
        self.joinCAN()
      elif var == "leave":
        pass
      elif var == "exit":
        self.server.stop()
        break
      elif var == "test":
        for i in range(1, 10):
          print self.server.get_index()


if __name__ == '__main__':
  ip = 'localhost'
  port = 8080

  node = Node('1', 'localhost', 8080, Handler)
  node.run()
