/*
 * src/tutorial/intset.c
 *
 ******************************************************************************
 This file contains routines that can be bound to a Postgres backend and
 called by the backend in the process of processing queries.  The calling
 format for these routines is dictated by Postgres architecture.
 ******************************************************************************/

#include "postgres.h"

#include "fmgr.h"
#include "libpq/pqformat.h"		/* needed for send/recv functions */
#include <stdio.h>
#include <stdlib.h>
#include <bsd/stdlib.h>
#include <limits.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>
#include <assert.h>

#ifndef HAVE_STRTONUM
#include <errno.h>

#define INVALID     1
#define TOOSMALL    2
#define TOOLARGE    3

//source from openbsd strtonum convert string to long long
long long
strtonum(const char *numstr, long long minval, long long maxval, const char **errstrp) {
    long long ll = 0;
    char *ep;
    int error = 0;
    struct errval {
        const char *errstr;
        int err;
    } ev[4] = {
        { NULL,     0 },
        { "invalid",    EINVAL },
        { "too small",  ERANGE },
        { "too large",  ERANGE },
    };

    ev[0].err = errno;
    errno = 0;
    if (minval > maxval)
        error = INVALID;
    else if (numstr[0] == '0' && strlen(numstr) != 1)
        error = INVALID;
    else {
        ll = strtol(numstr, &ep, 10);
        if (numstr == ep || *ep != '\0')
            error = INVALID;
        else if ((ll == LLONG_MIN && errno == ERANGE) || ll < minval)
            error = TOOSMALL;
        else if ((ll == LLONG_MAX && errno == ERANGE) || ll > maxval)
            error = TOOLARGE;
    }
    if (errstrp != NULL)
        *errstrp = ev[error].errstr;
    errno = ev[error].err;
    if (error)
        ll = 0;

    return (ll);
}

#endif /* HAVE_STRTONUM */

PG_MODULE_MAGIC;
// integer set
typedef struct Intset {
    int size;
    int capacity;
    int * vals;
}Intset;

//splice string by comma
typedef struct Slices{
    int size;
    int capacity;
    char ** pieces;
}Slices;


void trimEndSpace(char * str) ;
void newIntset(Intset* s, int c) ;
void newSlices(Slices* s, int c) ;
void addToSet(Intset * s, int n) ;
void split(Slices *slice, char * str) ;
void addPieceToSlices(Slices * s, char * p) ;
int parser_intset(char * str, Intset * out) ;
int cmpfunc (const void * a, const void * b) ;
char * intsetToString(Intset * set) ;
static Intset * intset_union_internal(Intset* a, Intset* b) ;
static int contains(int n, Intset * s) ;

void newIntset(Intset* s, int c) {
    if (s == NULL) return ;
    s->size = 0;
    s->capacity = c;
    s->vals = calloc(sizeof(int), s->capacity);
}

void newSlices(Slices* s, int c) {
    if (s == NULL) return ;
    s->size = 0;
    s->capacity = c;
    s->pieces = calloc(sizeof(char*), s->capacity);
}

int cmpfunc (const void * a, const void * b) {
    return ( *(int*)a - *(int*)b );
}

void addToSet(Intset * s, int n) {
    if (s == NULL) return;
    if (s->size == s->capacity) {
        s->capacity *= 2;
        s->vals = realloc(s->vals, sizeof(int) * s->capacity);
    }
    if (contains(n, s)) { return; }
    s->vals[s->size] = n;
    s->size ++ ;
    qsort(s->vals, s->size, sizeof(int), cmpfunc);
}

void addPieceToSlices(Slices * s, char * p){
    if (s == NULL) return;
    if (s->size == s->capacity) {
        s->capacity *= 2;
        s->pieces = realloc(s->pieces, sizeof(char*) * s->capacity);
    }
    s->pieces[s->size] = p;
    s->size ++ ;
}

void trimEndSpace(char * str) {
    int i, l = strlen(str);
    for(i = l - 1; i >= 0; i --) {
        if(!isspace(str[i])){
            break;
        } else {
            str[i]='\0';
        }
    }
}

void split(Slices *slice, char * str) {
    char * start = str+1;
    while (*start == ' ') start ++;
    for (char * cur = start; *cur != '\0'; cur++) {
        if(*cur == ',' || *cur == '}') {
            *cur = '\0';
            trimEndSpace(start);
            addPieceToSlices(slice, start);
            start = cur+1;
            while (*start == ' ') start ++;
        }
    }
}

int parser_intset(char * str, Intset * out) {
    int i, len = strlen(str);
    struct Slices slice;
    if (str[0] != '{') return 1;
    if (str[len-1] != '}') return 1;
    newSlices(&slice, len);

    split(&slice, str);
    if (slice.size == 1 && strlen(slice.pieces[0]) == 0) {
        printf("num input %d |-%s-| \n",slice.size, slice.pieces[0]);
        newIntset(out, slice.size);
    } else {
        const char * errstr;
        newIntset(out, slice.size);
        for(i = 0; i < slice.size; i ++) {
            int num = strtonum(slice.pieces[i], INT_MIN, INT_MAX, &errstr);
            if (errstr) {
                free(slice.pieces);
                return 1;
            } else {
                addToSet(out, num);
            }
        }
    }
    free(slice.pieces);
    return 0;
}

char * intsetToString(Intset * set) {
    char * str = NULL;
    if (set != NULL) {
        str = malloc(sizeof(char) * 14 * set->size + 2);
        if (set->size == 0 ) {
            sprintf(str, "{}");
        } else if (set->size == 1) {
            sprintf(str, "{%d}", set->vals[0]);
        } else {
            int i;
            char tmp[14];
            sprintf(str, "{%d", set->vals[0]);
            for(i = 1; i < set->size; i ++) {
                if ((i+1) == set->size) {
                    sprintf(tmp, ",%d}", set->vals[i]);
                } else {
                    sprintf(tmp, ",%d", set->vals[i]);
                }
                strcat(str, tmp);
            }
        }
        str = realloc(str, strlen(str));
    }
    return str;
}

/*****************************************************************************
 * Input/Output functions
 *****************************************************************************/

PG_FUNCTION_INFO_V1(intset_in);

    Datum
intset_in(PG_FUNCTION_ARGS)
{
    char	*str = PG_GETARG_CSTRING(0);
    Intset    *result = (Intset *) palloc(sizeof(struct Intset));

    if (parser_intset(str, result))
        ereport(ERROR,
                (errcode(ERRCODE_INVALID_TEXT_REPRESENTATION),
                 errmsg("invalid input syntax for intset: \"%s\"",
                     str)));

    PG_RETURN_POINTER(result);
}

PG_FUNCTION_INFO_V1(intset_out);

    Datum
intset_out(PG_FUNCTION_ARGS)
{
    Intset    *intset = (Intset *) PG_GETARG_POINTER(0);
    char   *result = intsetToString(intset);

    PG_RETURN_CSTRING(result);
}

/*****************************************************************************
 * New Operators
 *
 * A practical Intset datatype would provide much more than this, of course.
 *****************************************************************************/
static int contains(int n, Intset * s) {
    int b, e;
    if (s == NULL) return 0;
    if (s->size == 0) return 0;
    b = 0, e = s->size-1;
    if (n == s->vals[b] || n == s->vals[e]) {
        return 1;
    }
    if (n > s->vals[b] && n < s->vals[e]) {
        while(b <= e) {
            int m = (b + e)/2;
            if(s->vals[m] == n) {
                return 1;
            } else if(e != m && s->vals[m] > n) {
                e = m;
            } else if (b != m && s->vals[m] < n)  {
                b = m;
            } else {
                break;
            }
        }
    }
    return 0;
}

static int is_equal(Intset* a, Intset* b){
    int res = 0;
    if (a != NULL && b != NULL && a->size == b->size) {
        char * stra = intsetToString(a);
        char * strb = intsetToString(b);
        if (!strcmp(stra, strb)){
            res = 1;
        }
        free(stra);
        free(strb);
    }
    return res;
}

PG_FUNCTION_INFO_V1(intset_cardinality);

    Datum
intset_cardinality(PG_FUNCTION_ARGS)
{
    Intset    *a = (Intset *) PG_GETARG_POINTER(0);
    PG_RETURN_POINTER(a->size);
}

PG_FUNCTION_INFO_V1(intset_equal);

    Datum
intset_equal(PG_FUNCTION_ARGS)
{
    Intset    *a = (Intset *) PG_GETARG_POINTER(0);
    Intset    *b = (Intset *) PG_GETARG_POINTER(1);
    PG_RETURN_BOOL(is_equal(a,b) == 1);
}

PG_FUNCTION_INFO_V1(intset_subset);

    Datum
intset_subset(PG_FUNCTION_ARGS)
{
    Intset    *a = (Intset *) PG_GETARG_POINTER(0);
    Intset    *b = (Intset *) PG_GETARG_POINTER(1);
    int res = 0;
    if (a != NULL && b != NULL) {
        if(a->size == 0) {
            res = 1;
        } else if (a->size == b->size) {
            res = is_equal(a,b);
        } else if (a->size < b->size) {
            int i;
            for(i = 0; i< a->size; i++) {
                if(!contains(a->vals[i], b)) {
                    break;
                }
            }
            if (i == a->size) { res = 1; }
        }
    }
    PG_RETURN_BOOL(res);
}

static Intset * intset_union_internal(Intset* a, Intset* b) {
    Intset    *result = NULL;
    if (a != NULL && b != NULL) {
        int i;
        result = (Intset*) palloc(sizeof(struct Intset));
        newIntset(result,(a->size + b->size));

        if (a->size != 0 && b->size != 0) {
            int l = 0, r = 0;
            for(i = 0; i < result->capacity; ){
                if (l < a->size && r < b->size && a->vals[l] < b->vals[r]){
                    result->vals[i++] = a->vals[l++];
                } else if (l < a->size && r < b->size && a->vals[l] > b->vals[r]) {
                    result->vals[i++] = b->vals[r++];
                } else if (l < a->size && r < b->size && a->vals[l] == b->vals[r]) {
                    result->vals[i++] = b->vals[r++];
                    l++;//**required
                } else if (r < b->size) {
                    result->vals[i++] = b->vals[r++];
                } else if (l < a->size) {
                    result->vals[i++] = a->vals[l++];
                } else if (l == a->size && r == b->size) {
                    break;
                }
            }
            result->size = i;
        } else {
            int * set ;
            set = (a->size == 0)? b->vals : a->vals;
            result->size = (a->size == 0)? b->size : a->size;
            for(i = 0; i < result->size; i++) result->vals[i] = set[i];
        }
    }
    return result;
}

static Intset* intset_intersect_internal(Intset* a, Intset* b) {
    Intset * result = NULL;
    if (a != NULL && b != NULL) {
        result = (Intset*)palloc(sizeof(struct Intset));
        if (a->size == 0 || b->size == 0) {
            newIntset(result, 5);
        } else {
            int i;
            int s = (a->size < b->size)? a->size : b->size;
            int * set = (a->size < b->size)? a->vals : b->vals;
            Intset * tmp = (a->size < b->size)? b : a;
            newIntset(result, s);
            for(i = 0; i < s; i++) {
                if(contains(set[i],tmp)) {
                    addToSet(result, set[i]);
                }
            }
        }
    }
    return result;

}

PG_FUNCTION_INFO_V1(intset_contain);

    Datum
intset_contain(PG_FUNCTION_ARGS)
{
    int        n =  PG_GETARG_INT32(0);
    Intset    *a = (Intset *) PG_GETARG_POINTER(1);
    PG_RETURN_BOOL(contains(n,a) == 1);
}

PG_FUNCTION_INFO_V1(intset_union);

    Datum
intset_union(PG_FUNCTION_ARGS)
{
    Intset    *a = (Intset *) PG_GETARG_POINTER(0);
    Intset    *b = (Intset *) PG_GETARG_POINTER(1);
    PG_RETURN_POINTER(intset_union_internal(a, b));
}

PG_FUNCTION_INFO_V1(intset_intersect);

    Datum
intset_intersect(PG_FUNCTION_ARGS)
{
    Intset    *a = (Intset *) PG_GETARG_POINTER(0);
    Intset    *b = (Intset *) PG_GETARG_POINTER(1);
    PG_RETURN_POINTER(intset_intersect_internal(a, b));
}


PG_FUNCTION_INFO_V1(intset_disjunction);

    Datum
intset_disjunction(PG_FUNCTION_ARGS)
{
    Intset    *a = (Intset *) PG_GETARG_POINTER(0);
    Intset    *b = (Intset *) PG_GETARG_POINTER(1);
    Intset    *result = NULL;
    if (a != NULL && b != NULL) {
        if (a->size == 0 || b->size == 0){
            result = intset_union_internal(a, b);
        } else {
            int i, l=0, r=0;
            result = (Intset*) palloc(sizeof(struct Intset));
            newIntset(result, (a->size+b->size));
            for(i = 0; i < result->capacity;) {
                if(l < a->size && !contains(a->vals[l++],b)) {
                    addToSet(result, a->vals[l-1]);
                }
                if(r < b->size && !contains(b->vals[r++],a)) {
                    addToSet(result, b->vals[r-1]);
                }
                if (l == a->size && r == b->size) {
                    break;
                }
            }
        }
    }
    PG_RETURN_POINTER(result);
}

PG_FUNCTION_INFO_V1(intset_difference);

    Datum
intset_difference(PG_FUNCTION_ARGS)
{
    Intset    *a = (Intset *) PG_GETARG_POINTER(0);
    Intset    *b = (Intset *) PG_GETARG_POINTER(1);
    Intset    *result = NULL;
    if (a != NULL && b != NULL) {
        if (a->size == 0) {
            result = intset_intersect_internal(a, b);
        } else if (b->size == 0){
            result = intset_union_internal(a, b);
        } else {
            int i;
            result = (Intset*) palloc(sizeof(struct Intset));
            newIntset(result, (a->size));
            for(i = 0; i < a->size; i++) {
                if(!contains(a->vals[i],b))
                    addToSet(result, a->vals[i]);
            }
        }
    }

    PG_RETURN_POINTER(result);
}


