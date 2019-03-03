#! /usr/bin/python3
import math

def existSum(A, x):
  if len(A) == 0:
    return False
  res = False
  A = sorted(A)
  for i in range(0,len(A)):
    t = x - A[i]
    if t < A[0] or  t> A[-1]:
      continue
    if t in A:
      res = True
      break
  return res

A = [0,1,5,2,3,5,6]
target = -2
print(existSum(A, target))
