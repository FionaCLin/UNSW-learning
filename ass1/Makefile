CFLAGS=-O -Wall -Werror

TARGETS	:= cs3121_ass1

.PHONY: all clean

cs3121_ass1.pdf: cs3121_ass1.tex
	latexmk -pdf cs3121_ass1

clean:
	$(RM) $(TARGETS)
	latexmk -C cs3121_ass1
