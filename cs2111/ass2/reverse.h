/* $Revision: 1.3 $ */
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>

/* reverse the decimal representation of n
   proc reversen(value n:nat, result r:nat).
     \CON~S:[10]^*\cdot r:[n=\sum_{i=0}^{c(n)}(S_i 10^{(c(n)-i)}) \And n>0,
                           r=\sum_{i=0}^{c(n)}(S_i 10^i)]
   where c(n) = \floor{\log_{10}n} % that's why n=0 is excluded
*/

#ifndef USEGMP
void reversen(unsigned long n, unsigned long *r);
#else
#include <gmp.h>
void reversen(mpz_t n, mpz_t r);
#endif

/* ugly global vars for doing the reversing, big enough to hold the largest known emirps */
char _reversen_inbuf[11111], _reversen_outbuf[11111];

