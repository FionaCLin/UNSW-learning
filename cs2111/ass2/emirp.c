#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <gmp.h>
#define USEGMP

#include "reverse.h"
#define FALSE 0
#define TRUE 1

void emirp(mpz_t n, mpz_t r) {
    /* 1. Initialize the beginning prime number s as 13 */
    mpz_t s;
    mpz_init(s);
    mpz_set_ui(s, 13);

    // decrement n till n equal to 0
    while (mpz_cmp_ui(n, 0) != 0) {
        /* 3. reverse s put into  r*/
        reversen(s, r);
        /* 4. check if r is prime number and r is not same as s */
        if (mpz_probab_prime_p(r, 40) && mpz_cmp(r, s)) {
            /* 5. decrement the n when r is prime*/
            mpz_sub_ui(n, n, 1);
        }
        /* 6. get the next prime number while n =! 0 */
        mpz_nextprime(s, s);
    }
    /* while n = 0, reverse r as nth prime is s and return r as result */
    reversen(r, r);
}

int main() {
    char inputStr[1024];
    //   mpz_t is the type defined for GMP integers.
    //   It is a pointer to the internals of the GMP integer data structure
    mpz_t n;
    int flag;

    printf("Enter your number: ");
    flag = scanf("%1023s", inputStr);
    // NOTE: never every write a call scanf ("%s", inputStr);
    // You are leaving a security hole in your code.
    assert(flag > 0);
    // If flag is greater 0 then the operation /*

    // 1. Initialize the number
    mpz_init(n);
    mpz_set_ui(n, 0);

    // 2. Parse the input string as a base 10 number
    flag = mpz_set_str(n, inputStr, 10);
    assert(flag == 0);
    // If flag is not 0 then the operation /*

    // 3. Initialize the result number
    mpz_t r;
    mpz_init(r);

    // 4. find the nth number that reversed also is prime
    emirp(n, r);
    mpz_out_str(stdout, 10, r);
    printf("\n");
    // 6. Clean up the mpz_t handles or else we will leak memory
    mpz_clear(n);
    return EXIT_SUCCESS;
}
