#include <stdio.h>
#include <stdlib.h>
#include "uniq.h"

#define MALLOCWOES 1
#define MAXLINES 1024
#define BUFLENGTH 1024

void barf (int errorcode);

int main(int argc, char *argv[]) {
  char *a[MAXLINES], *b[MAXLINES];
  unsigned int i, n, k;
  int more = 1;
  
  for (i = 0; i < MAXLINES; i++)
    if (NULL == (a[i] = malloc(BUFLENGTH)) ||
        NULL == (b[i] = malloc(BUFLENGTH)))
      barf(MALLOCWOES);
  
  for (n = 0; n < MAXLINES && more; n++)
    if (NULL == fgets (a[n], BUFLENGTH, stdin))
      more = 0;

  k = uniq (n, a, b);

  for (i = 0; i < k; i++)
    fputs (b[i], stdout);

  for (i = 0; i < MAXLINES; i++) {
    free (a[i]);
    free (b[i]);
  }
  
  return EXIT_SUCCESS;
}

void barf(int errorcode) {
  switch (errorcode) {
  case MALLOCWOES:
    fprintf(stderr, "Error: failed to allocate memory.\n");
  default:
    fprintf(stderr, "Error: died, unknown cause.\n");
  }
  exit(EXIT_FAILURE);
}
