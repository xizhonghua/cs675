from point import *


class Zone():

  def __init__(self, x, y, width, height):
    self.x = x
    self.y = y
    self.width = width
    self.height = height

  def __str__(self):
    return str(self.x) + ', ' + str(self.y) + ', ' + \
        str(self.width) + ', ' + str(self.height)

  '''
  Check whether a point is in the zone
  '''

  def contains(self, point):
    if point.x >= self.x + self.width \
            or point.y >= self.y + self.height \
            or point.x < self.x or point.y < self.y:
      return False
    return True

  def is_square(self):
    return self.width == self.height

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
  print 'zone:', zone
  p1 = Point(1, 1)
  print 'p1 =', p1, 'zone.contains(p1) =', zone.contains(p1)
  p2 = Point(10, 3)
  print 'p2 =', p2, 'zone.contains(p2) =', zone.contains(p2)

  while True:
    new_zone = zone.split()
    print 'new_zone:', new_zone
    if new_zone.width == 1 and new_zone.height == 1:
      break
