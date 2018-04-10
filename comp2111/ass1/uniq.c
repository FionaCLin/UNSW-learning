#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "uniq.h"

unsigned int cmp(char *a, char *b) ;

unsigned int uniq(unsigned int n, char *a[], char *b[]) {
    int k = 0;
    for(int i = 0; i < n; i ++) {
        if (i == 0) {
            b[i] = malloc(strlen(a[i]));
            strcpy(b[i], a[i]);
        } else {
            int h = 0;
            for (; a != NULL && a[i][h] != '\0' && b != NULL && b[k][h] == a[i][h]; h++);
            if (a[i][h] != b[k][h]) {
                b[++k] = malloc(strlen(a[i]));
                strcpy(b[k],a[i]);
            }
        }
    }
    return k;
}

