CFLAGS=-O -Wall -Werror

CFILES	:= $(wildcard *.c)
OFILES	:= $(patsubst %.c,%.o,$(CFILES))
TARGETS	:= emirp

.PHONY: all clean

all: emirp

# remove -DUSEGMP and -lgmp unless you use gmp
emirp: reverse.c reverse.h emirp.c
	gcc $(CFLAGS) -DUSEGMP -c reverse.c 
	gcc $(CFLAGS) reverse.o emirp.c -o emirp -lgmp 

emirp.pdf: emirp.tex
	latexmk -pdf emirp

clean:
	$(RM) $(OFILES) $(TARGETS)
	latexmk -C emirp
