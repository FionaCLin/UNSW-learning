#-------------------------------------------------------------------------
#
# Makefile--
#    Makefile for tutorial
#
# By default, this builds against an existing PostgreSQL installation
# (the one identified by whichever pg_config is first in your path).
# Within a configured source tree, you can say "make NO_PGXS=1 all"
# to build using the surrounding source tree.
#
# IDENTIFICATION
#    src/tutorial/Makefile
#
#-------------------------------------------------------------------------

#CC=gcc
CC=clang

#CFLAGS=-Wall -Werror -gdwarf-2
CFLAGS=-Wall -Werror -g
.PHONY: clean clobber

all: x x.o

x: 	x.o
	$(CC) -o x  x.o

x.o: 	x.c
	$(CC) -c $(CFLAGS) x.c

clean:
	rm -f *.o

clobber: clean
		rm x.c



















