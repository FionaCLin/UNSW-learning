CFLAGS=-O -Wall -Werror

TARGETS	:= mid-06

.PHONY: all clean

mid-06.pdf: mid-06.tex
	latexmk -pdf mid-06

clean:
	$(RM) $(TARGETS)
	latexmk -C mid-06
