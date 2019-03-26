CFLAGS=-O -Wall -Werror

TARGETS	:= cs3121_ass2  __latexindent_temp.tex

.PHONY: all clean

cs3121_ass2.pdf: cs3121_ass2.tex
	latexmk -pdf cs3121_ass2

clean:
	$(RM) $(TARGETS)
	latexmk -C cs3121_ass2
