#!/usr/bin/env python3
#
word = 'hello'
print(len(word))
print(len('hello'))
thingo = [3, 5, 6 + 3]
thingo[1] = 15
thingo.append(100)
x = thingo.pop()
thingo.append(x)
print(x)
x = thingo.pop()
print(x)
print(len(thingo))

book = 'this book\'s name is "difficult"'
print(book)
print(len(book))

print('this book\'s name is "difficult"')
