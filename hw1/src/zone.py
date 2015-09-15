from point import *


class Zone(object):

  def __init__(self, x=0, y=0, width=0, height=0):
    self.x = x
    self.y = y
    self.width = width
    self.height = height

  def __repr__(self):
    return self.__str__()

  def __str__(self):
    return str(self.x) + ', ' + str(self.y) + ', ' + \
        str(self.width) + ', ' + str(self.height)

  def set(self, x, y, width, height):
    self.__init__(x, y, width, height)
    return self

  def setFromDict(self, zone):
    self.set(int(zone['x']),
             int(zone['y']),
             int(zone['width']),
             int(zone['height']))
    return self

  def clone(self):
    return Zone(self.x, self.y, self.width, self.height)

  '''
  Check whether a point is in the zone
  '''

  def contains(self, point):
    if point.x >= self.x + self.width \
            or point.y >= self.y + self.height \
            or point.x < self.x or point.y < self.y:
      return False
    return True

  '''
  Compute the distance to a point
  '''

  def dist(self, point):
    if self.contains(point):
      return 0

    dx = point.x - self.x + self.width / 2
    dy = point.y - self.y + self.height / 2

    return dx ** 2 + dy ** 2

  def is_square(self):
    return self.width == self.height

  '''
  Check whether two zones are neighbors
  '''

  def is_neightbor(self, zone):
    for i in range(0, 4):
      for j in range(0, 4):
        if self._edge_overlap(self._get_edge(i), zone._get_edge(j)):
          return True
    return False

  '''
  Return an edge (Point, Point) of the zone
  index: 0 top, 1 right, 2 bottom, 3 left
  points in edge is from left to right, from top to bottom
  '''

  def _get_edge(self, index):
    if index == 0:
      return (Point(self.x, self.y),
              Point(self.x + self.width, self.y))
    if index == 1:
      return (Point(self.x + self.width, self.y),
              Point(self.x + self.width, self.y + self.height))
    if index == 2:
      return (Point(self.x, self.y + self.height),
              Point(self.x + self.width, self.y + self.height))
    if index == 3:
      return (Point(self.x, self.y),
              Point(self.x, self.y + self.height))

  '''
  Check whether two edges overlap
  '''

  def _edge_overlap(self, edge1, edge2):
    # a, b = edge1
    # c, d = edge2
    inter_len = self._edge_intersect_len(edge1, edge2)
    # print a, b, c, d, inter_len
    return inter_len > 0
    # ( self._point_in_edge(edge1, c) \
    # or self._point_in_edge(edge1, d) \
    # or self._point_in_edge(edge2, a) \
    # or self._point_in_edge(edge2, b) )

  def _edge_intersect_len(self, edge1, edge2):
    a, b = edge1
    c, d = edge2

    # horizontal
    if a.y == b.y and a.y == c.y and a.y == d.y:
      sx = max(a.x, c.x)
      tx = min(b.x, d.x)
      return tx - sx

    if a.x == b.x and a.x == c.x and a.x == d.x:
      sy = max(a.y, c.y)
      ty = min(b.y, d.y)
      return ty - sy

    return 0

  '''
  Check whether a point in edge
  '''

  def _point_in_edge(self, edge, p):
    a, b = edge

    # horizontal
    if a.y == b.y and a.y == p.y:
      if p.x >= a.x and p.x <= b.x:
        return True

    # vertical
    if a.x == b.x and a.x == p.x:
      if p.y >= a.y and p.y <= b.y:
        return True

    return False

  '''
  Split the zone to half.
  Modify:
    self
  Return:
    the split zone
  '''

  def split(self):
    new_x = self.x
    new_y = self.y
    new_w = self.width
    new_h = self.height

    if self.is_square():
      new_w = self.width / 2
      new_x = self.x + self.width - new_w
      self.width -= new_w
    else:
      new_h = self.height / 2
      new_y = self.y + self.height - new_y
      self.height -= new_h

    new_zone = Zone(new_x, new_y, new_w, new_h)
    return new_zone

  '''
  Merge two zones.
  Update:
    self
  '''

  def merge(self, zone):
    pass


if __name__ == '__main__':
  zone = Zone(0, 0, 10, 10)
  print 'zone =', zone
  print 'zone.__dict__ =', zone.__dict__
  p1 = Point(1, 1)
  print 'p1 =', p1, 'zone.contains(p1) =', zone.contains(p1)
  p2 = Point(10, 3)
  print 'p2 =', p2, 'zone.contains(p2) =', zone.contains(p2)

  while True:
    new_zone = zone.split()
    print 'new_zone:', new_zone
    if new_zone.width == 1 and new_zone.height == 1:
      break
  print '--------------------------------'
  print 'test is_neightbor'
  zone1 = Zone(0, 0, 10, 10)
  zone2 = Zone(10, 0, 10, 10)
  print 'expected = True, actual =', zone1.is_neightbor(zone2)
  print 'expected = True, actual =', zone2.is_neightbor(zone1)
  zone3 = Zone(10, 10, 5, 5)
  print 'expected = False, actual =', zone1.is_neightbor(zone3)
  print '--------------------------------'
