CFLAGS=-O -Wall -Werror

TARGETS	:= cs3121_ass3  __latexindent_temp.tex cs3121_ass3.synctex.gz

.PHONY: all clean

cs3121_ass3.pdf: cs3121_ass3.tex
	latexmk -pdf cs3121_ass3

clean:
	$(RM) $(TARGETS)
	latexmk -C cs3121_ass3
