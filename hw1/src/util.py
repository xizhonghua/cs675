import random
from setting import *
from point import *

random.seed()


def hash(keyword):
  x = 0
  y = 0
  for i in xrange(len(keyword)):
    char = ord(keyword[i])
    if i % 2 == 1:
      x += char
    else:
      y += char
  x %= ZONE_MAX_WIDTH
  y %= ZONE_MAX_HEIGHT
  p = Point(x, y)
  return p

'''
Generate a random point in the Zone space
'''


def random_point():
  x = random.randint(0, ZONE_MAX_WIDTH - 1)
  y = random.randint(0, ZONE_MAX_HEIGHT - 1)
  p = Point(x, y)
  return p

if __name__ == '__main__':
  print hash('000002')
  print random_point()
