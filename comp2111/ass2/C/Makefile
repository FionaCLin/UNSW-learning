CFLAGS=-O -Wall -Werror

CFILES	:= $(wildcard *.c)
OFILES	:= $(patsubst %.c,%.o,$(CFILES))
TARGETS	:= quicksorttest genrand

.PHONY: all clean

all: $(TARGETS)

quicksorttest: quicksort.c quicksort.h quicksorttest.c
	$(CC) $(CFLAGS) -c quicksort.c 
	$(CC) $(CFLAGS) quicksort.o quicksorttest.c -o quicksorttest

quicksort.pdf: quicksort.tex
	latexmk -pdf quicksort

clean:
	$(RM) $(OFILES) $(TARGETS)
	latexmk -C quicksort
