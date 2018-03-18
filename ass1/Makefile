CFLAGS=-O -Wall -Werror

CFILES	:= $(wildcard *.c)
OFILES	:= $(patsubst %.c,%.o,$(CFILES))
TARGETS	:= uniqtest

.PHONY: all clean

all: $(TARGETS)

uniqtest: uniq.c uniq.h uniqtest.c
	$(CC) $(CFLAGS) -c uniq.c 
	$(CC) $(CFLAGS) uniq.o uniqtest.c -o uniqtest

uniq.pdf: uniq.tex
	latexmk -pdf uniq

clean:
	$(RM) $(OFILES) $(TARGETS)
	latexmk -C uniq
