#!/usr/bin/python3


a = {'hello': 'world', 'take': 'apples'}
a['take'] ='bananas'

print("%s %s %s %d" % ('h\'a"l"lo', a['take'], a['hello'], 4))

for key in a.keys() :
    print(a[key])

c = sorted(a.keys())
len(c)
print(c)
