#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "uniq.h"

unsigned int cmp(char *a, char *b) ;

unsigned int uniq(unsigned int n, char *a[], char *b[]) {
    int k = 0;
    /*Here opted for the more traditional for loop idiom instead of a while loop.*/
    for(int i = 0; i < n; i ++) {
        /*first if i = 0 branch to string copy the first element to string array b*/
        if (i == 0) {
            /*copy the first element into b[0]*/
            b[i] = malloc(strlen(a[i]));
            strcpy(b[i], a[i]);
        } else {
        /*first if else branch to string compare the first */
            int h = 0;
            /*In c implementation, checking individual characters 
            in both string arrays to preform the string compare. 
            Here opted for a and b pointer nullity checking, and 
             characters comparison as well as h increment */
            for (; a != NULL && a[i][h] != '\0' && b != NULL && b[k][h] == a[i][h]; h++);
            /*Check if exist h make a[i][h] and b[k][h] different */
            if (a[i][h] != b[k][h]) {
            /*increment k and alloction memory in c implementation*/
                b[++k] = malloc(strlen(a[i]));
            /*assign a[i] to b[k]*/
                strcpy(b[k],a[i]);
            }
        }
    }
    return k;
}

