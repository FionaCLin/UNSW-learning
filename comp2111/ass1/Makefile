#CFLAGS=-O -Wall -Werror -DDEBUG
CFLAGS=-O -Wall -Werror

CFILES	:= $(wildcard *.c)
OFILES	:= $(patsubst %.c,%.o,$(CFILES))
TARGETS	:= uniqtest
TESTS   := $(wildcard tests/*.txt)
RESULTS	:= $(patsubst tests/%,results/%,$(TESTS))
LITMUS	:= $(patsubst tests/%,litmus/%,$(TESTS))

.PHONY: all results clean litmus

all: $(TARGETS)
results: $(RESULTS)
	mkdir -p results
litmus: $(LITMUS)
	mkdir -p litmus

uniqtest: uniq.c uniq.h uniqtest.c
	$(CC) $(CFLAGS) -c uniq.c 
	$(CC) $(CFLAGS) uniq.o uniqtest.c -o uniqtest

uniq.pdf: uniq.tex
	latexmk -pdf uniq

$(RESULTS): uniqtest
$(RESULTS): results/%.txt: tests/%.txt
	./uniqtest < $< > $@

$(LITMUS): litmus/%.txt: results/%.txt
$(LITMUS): litmus/%.txt: tests/%.txt
	uniq < $< > $@
	-diff $(patsubst tests/%,results/%,$<) $@
clean:
	$(RM) $(OFILES) $(TARGETS) $(RESULTS)
	latexmk -C uniq
