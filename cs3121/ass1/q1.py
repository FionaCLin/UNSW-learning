#! /usr/bin/python3
import math

def numOfa(A, L_k, R_k):
  if len(A) == 0:
    return 0
  inLk = binarySearchIndex(A, L_k)
  inRk = binarySearchIndex(A, R_k)
  if A[inRk] in A and A[inLk] in A:
    return inRk - inLk + 1
  else:
    return inRk - inLk

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

A = [0,1,2,4,5,6]
print(numOfa(A, 3, 5))
print(numOfa(A, 4, 5))

# A = [0,1,2,4,5,6]
# target = 7
# index = binarySearchIndex(A, target)
# print(target, A[index])
# target = 3
# index = binarySearchIndex(A, target)
# print(target, A[index])
# target = 0
# index = binarySearchIndex(A, target)
# print(target, A[index])
# target = 6
# index = binarySearchIndex(A, target)
# print(target, A[index])
# target = 1
# index = binarySearchIndex(A, target)
# print(target, A[index])
# target = 4
# index = binarySearchIndex(A, target)
# print(target, A[index])
