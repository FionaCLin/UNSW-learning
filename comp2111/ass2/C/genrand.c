/* 
 * usage: genrand <n>
 * generates a pseudorandom input array of length <n> for quicksorttest
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int
main (int argc, char *argv[])
{
  int i, N;
  srand (time (NULL));
  if (argc < 2 || (N = atoi(argv[1])) <= 0) 
    N = 1000;                   /* fallback */
  printf("%d\n", N);
  for (i=1; i<=N; i++)
    printf("%d%c", rand() - RAND_MAX/2, i%16 ? ' ':'\n');
  printf ("\n");

  return EXIT_SUCCESS;
}
