#include <stdio.h>
#include <stdlib.h>

#include "bfs.h"

#define DEFAULTN 5
#define MAXDEPTH 1024

int main (int argc, char *argv[]) {
  int N, ll, c;
  Key k, *l;
  T p;
  Tree root = NULL;
    
  if (argc != 2 || 0 >= (N = atoi (argv[1]))) {
    nag ("expecting a single non-negative argument for the queue size)");
    fprintf(stderr, "... will use default %d instead.\n", N = DEFAULTN);
  }

  if (NULL == (l = (Key *) malloc (MAXDEPTH * sizeof(Key))))
    barf("failing in malloc");
  
  while ((c = getchar ()) != EOF && c != 'q')
    switch (c) {
    case 'i':                   /* insert */
      if (Success != readKey(&k))
        nag ("failing to read a key for inserting a node");
      else if (Success != readT(&p))
        nag ("failing to read a payload for inserting a node");
      else if (Success != readKeyList(&ll, l, MAXDEPTH))
        nag ("failing to read a path for inserting a node");
      else if (Success != tinsert(k, p, ll, l, &root))
        nag ("failing to insert a node");
      break;
    case 'p':                   /* print */
      tprint(root);
      break;
    case 's':                   /* search */
      if (Success != readKey(&k))
        nag ("failing to read a key for searching");
      else {
        RetVal found;
        search(root, N, k, &p, &found);
        if (found) {
          printf("Found payload ");
          printT(p);
          printf(" for key ");
          printKey(k);
        } else {
          printf("Key ");
          printKey(k);
          printf(" could not be found");
        }
        printf(".\n");
      }
    }
  free (l);
  return EXIT_SUCCESS;
}
