#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "uniq.h"

unsigned int uniq(unsigned int n, char *a[], char *b[]) {
    int j = 0;
    b[0] = malloc(strlen(a[0]));
    strcpy(b[0],a[0]);
    for(int i = 1; i < n; i ++) {
        if(strcmp(b[j], a[i]) != 0) {
            j++;
            b[j] = malloc(strlen(a[i]));
            strcpy(b[j],a[i]);
        }
    }
    return j;
}

