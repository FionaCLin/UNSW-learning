#include <stdio.h>
#include <stdlib.h>
#include "sof.h"

#define ARGCERROR -1

void barf (int errorcode, char *argv[]);

int main(int argc, char *argv[]) {
  unsigned long t,a,b;

  if (argc != 4)
    barf(ARGCERROR, argv);
  t = strtoul(argv[1],NULL,10);
  a = strtoul(argv[2],NULL,10);
  b = strtoul(argv[3],NULL,10);

  printf ("%lu\n", sof(t,a,b));

  return EXIT_SUCCESS;
}

void barf(int errorcode, char *argv[]) {
  switch (errorcode) {
  case ARGCERROR:
    fprintf(stderr, "Error: usage %s <t> <a> <b>.\n", argv[0]); break;
  default:
    fprintf(stderr, "Error: died, unknown cause.\n");
  }
  exit(EXIT_FAILURE);
}
