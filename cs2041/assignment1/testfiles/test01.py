#!/usr/bin/python3
import sys

number = 0
while number >= 0:
    sys.stdout.write("> ")
    number = int(sys.stdin.readline())
    if number >= 0:
        if number % 2 == 0:
            print("Even")
        else:
            print("Odd")
print("Bye")

lines = sys.stdin.readlines()
line_count = len(lines)
print("%d lines" % line_count)


### test sys.stdin.*
#output:
#
#
###


