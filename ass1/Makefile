CFLAGS=-O -Wall -Werror

CFILES	:= $(wildcard *.c)
OFILES	:= $(patsubst %.c,%.o,$(CFILES))
TARGETS	:= cs3121_ass1

.PHONY: all clean

cs3121_ass1.pdf: cs3121_ass1.tex
	latexmk -pdf cs3121_ass1

clean:
	$(RM) $(OFILES) $(TARGETS)
	latexmk -C emirp
