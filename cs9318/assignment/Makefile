CFLAGS=-O -Wall -Werror

TARGETS	:= ass ass.synctex.gz

.PHONY: all clean

ass.pdf: ass.tex
	latexmk -pdf ass

clean:
	$(RM) $(TARGETS)
	latexmk -C ass
