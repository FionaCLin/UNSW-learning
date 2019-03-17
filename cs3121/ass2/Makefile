CFLAGS=-O -Wall -Werror

TARGETS	:= cs3121_ass2

.PHONY: all clean

cs3121_ass2.pdf: cs3121_ass2.tex
	latexmk -pdf cs3121_ass2

clean:
	$(RM) $(TARGETS)
	latexmk -C cs3121_ass2
