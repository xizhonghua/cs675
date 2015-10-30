import time

# return the seconds since epoch in string format


def now():
  return "{:.9f}".format(time.time())


if __name__ == "__main__":
  print now()
