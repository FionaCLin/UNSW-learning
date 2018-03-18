CFLAGS=-O -Wall -Werror
CFILES	:= $(wildcard *.c)
OFILES	:= $(patsubst %.c,%.o,$(CFILES))
TARGETS	:= bfstest 

.PHONY: all clean

all: $(TARGETS)

bfstest: bfs.c bfs.h bbq.c bbq.h bfstest.c
	$(CC) $(CFLAGS) -c bbq.c 
	$(CC) $(CFLAGS) -c bfs.c 
	$(CC) $(CFLAGS) bbq.o bfs.o bfstest.c -o bfstest

bfs.pdf: bfs.tex
	latexmk -pdf bfs

clean:
	$(RM) $(OFILES) $(TARGETS)
	latexmk -C bfs
