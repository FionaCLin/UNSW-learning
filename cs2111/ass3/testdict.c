#include <stdio.h>
#include <stdlib.h>
#include "dict.h"

#define BUFSIZE 1024

int main (int argc, char *argv[])
{
  char *line = NULL, w[BUFSIZE], c;
  size_t linecap = 0;

  /* init */
  Dict T;
  newdict(&T);

  /* command loop */
  while (getline (&line, &linecap, stdin) > 0) {
    if (line[0] == 'q')
      return EXIT_SUCCESS;
    else if (2 == sscanf(line, "%c %s", &c, w)) {
      switch (c) {
      case 'a':
        addword (T, w);
         printf("New word added: %s\n", w);
        break;
      case 'c':
        if (checkword (T, w))
          printf ("'%s' is known\n", w);
        else
          printf ("'%s' is not known\n", w);
        break;
      case 'd':
        if (checkword(T, w))  /* check precondition */
          delword (T, w);
        break;
      default:
        barf("failed to read a command.");
      }
    } else
      barf("failed to read a word.");
  };
  return EXIT_SUCCESS;
}
