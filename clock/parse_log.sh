#!/bin/bash
echo 'extracting cdr...'
cat sync.log | sed 1d | cut -d' ' -f29 > cdr.txt

echo 'computing mean rrt...'
echo "Avg RTT = `cat sync.log | sed 1d | cut -d' ' -f16 | st --mean`"

echo "Sent/Received = " `tail sync.log -n 1 | cut -d' ' -f32,35`
