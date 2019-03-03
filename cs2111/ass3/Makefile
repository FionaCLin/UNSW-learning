CFLAGS=-O -Wall -Werror

CFILES	:= $(wildcard *.c)
OFILES	:= $(patsubst %.c,%.o,$(CFILES))
TARGETS	:= testdict

.PHONY: all clean

all: testdict

testdict: dict.c dict.h testdict.c
	$(CC) $(CFLAGS) -c dict.c
	$(CC) $(CFLAGS) dict.o testdict.c -o testdict

dict.pdf: dict.tex
	latexmk -pdf dict

clean:
	$(RM) $(OFILES) $(TARGETS)
	latexmk -C dict
