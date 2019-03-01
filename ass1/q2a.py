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
    if binarySearchIndex(A, t) == t:
      res = True
      break
  return res

def binarySearchIndex(A, target):
  low = 0
  hig = len(A)
  mid = math.floor(hig/2)
  while target != A[mid]:
    if low != mid and A[mid] < target:
      low = mid
    elif hig != mid and A[mid] > target:
      hig = mid
    else:
      break
    mid = math.floor((hig + low)/2)
  return mid

A = [0,1,5,2,3,5,6]
target = 12
print(existSum(A, target))
