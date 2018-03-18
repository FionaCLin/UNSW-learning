#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/resource.h>
#include <time.h>

#include "quicksort.h"

/* begin from (1) <http://stackoverflow.com/questions/5248915/execution-time-of-c-program> */
#define CPU_TIME (getrusage(RUSAGE_SELF,&ruse), ruse.ru_utime.tv_sec +  \
  ruse.ru_stime.tv_sec + 1e-6 * \
  (ruse.ru_utime.tv_usec + ruse.ru_stime.tv_usec))
/* end from (1) */

#define LENGTHREADERROR -1
#define MEMERROR        -2
#define CELLREADERROR   -3

void barf (int errorcode, int extra);
int compare_ints(const void *p, const void *q);

int main(int argc, char *argv[]) {
  int n;
  int *a, *b;
  int i;

  struct rusage ruse;
  /* begin from (1) */
  time_t start, end;
  double first, second;
  /* end from (1) */

  if (1 != scanf("%d", &n) || n < 0)
    barf(LENGTHREADERROR, 0);

  if (NULL == (a = malloc(n * (sizeof *a))))
    barf(MEMERROR, 0);
  if (NULL == (b = malloc(n * (sizeof *b))))
    barf(MEMERROR, 1);

  for (i=0; i<n; i++)
    if (1 != scanf("%u", a+i))
      barf(CELLREADERROR, i);
  memcpy(b, a, n * sizeof(int));

  printf("Timing user code:\n");
  /* begin from (1) */
  time(&start);
  first = CPU_TIME;
  /* end from (1) */
  quicksort(a, 0, n-1, n);
  /* begin from (1) */
  time(&end);
  second = CPU_TIME;
  printf("cpu  : %.2f secs\n", second - first); 
  printf("user : %d secs\n", (int)(end - start));  
  /* end from (1) */

  printf("Timing qsort for comparison:\n");
  /* begin from (1) */
  time(&start);
  first = CPU_TIME;
  /* end from (1) */
  qsort(b, n, sizeof *b, &compare_ints);
  /* begin from (1) */
  time(&end);
  second = CPU_TIME;
  printf("cpu  : %.2f secs\n", second - first); 
  printf("user : %d secs\n", (int)(end - start));  
  /* end from (1) */

  /* more testing */
  if (0 != memcmp (a, b, n * (sizeof *b)))
    printf("Sorting error detected.\n");
      
  return EXIT_SUCCESS;
}

void barf(int errorcode, int extra) {
  switch (errorcode) {
  case LENGTHREADERROR:
    fprintf(stderr, "Error: failed reading a non-negative array length.\n"); break;
  case MEMERROR:
    fprintf(stderr, "Error: failed allocating memory for array %c\n", 'a' + extra); break;
  case CELLREADERROR:
    fprintf(stderr, "Error: failed reading array cell a[%d].\n", extra); break;
  default:
    fprintf(stderr, "Error: died, unknown cause.\n");
  }
  exit(EXIT_FAILURE);
}

/* copied from https://en.wikipedia.org/wiki/Qsort */
/* Comparison function. Receives two generic (void) pointers to the items under comparison. */
int compare_ints(const void *p, const void *q) {
    int x = *(const int *)p;
    int y = *(const int *)q;

    /* Avoid return x - y, which can cause undefined behaviour
       because of signed integer overflow. */
    if (x < y)
        return -1;  // Return -1 if you want ascending, 1 if you want descending order. 
    else if (x > y)
        return 1;   // Return 1 if you want ascending, -1 if you want descending order. 

    return 0;
}
