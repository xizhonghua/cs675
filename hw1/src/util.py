from setting import *


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
  return x, y

if __name__ == '__main__':
  print hash('000002')
