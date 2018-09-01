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

void test_valid() ;
void test_invalid() ;
void test_contains() ;
void print_intset(Intset * set);
void newSlices(Slices* s, int c) ;
void newIntset(Intset* s, int c) ;
void split(Slices *slice, char * str) ;
void addPieceToSlices(Slices * s, char * p) ;
void addToSet(Intset * s, int n) ;
char * intset_convert(Intset * set) ;
int parser_intset(char * str, Intset * out) ;
int test(char* s);
int contains(int n, Intset * s) ;
int is_subset(Intset * a, Intset * b) ;

void testContain(char *str) {
    struct Intset set;
    int res = parser_intset(str, &set);
    if (res) {
        print_intset(&set);
        if (set.size > 0) {
            assert(contains(set.vals[0], &set) == 1);
            printf("%d in\t", set.vals[0]);
            assert(contains(set.vals[set.size/2], &set) == 1);
            printf("%d in\t", set.vals[set.size/2]);
            assert(contains(set.vals[(set.size-1)], &set) == 1);
            printf("%d in\t", set.vals[set.size-1]);
            assert(contains(9999, &set) == 0);
            printf("9999 not in\n");
        } else {
            assert(contains(10, &set) == 0);
            printf("10 not in empty set %d\n", set.size);
        }
    }
    free(set.vals);
}

void test_contains() {
    char str []= "{ };{};{1   };{   1};{2,1,3,1};{2,1,3,3};{2,3,1};{6,6,6,6,6,6};{10, 9, 8, 7, 6,5,4,3,2,1};{1, 999, 13, 666, -5};{    1  ,  3  ,  5 , 7,9 };";
    char * start = str;
    for (char * cur = start; *cur != '\0'; cur++) {
        if(*cur == ';') {
            *cur = '\0';
            printf("\ntest -- %s --\n", start);
            testContain(start);
            start = cur+1;
        }
    }
}

void print_intset(Intset * set){
    char * output = intset_convert(set);
    printf("%s \t", output);
    printf("size %d, cap %d\n", set->size, set->capacity);
    free(output);
}


void test_valid() {
    char str []= "{ };{};{1   };{   1};{2,1,3,1};{2,1,3,3};{2,3,1};{6,6,6,6,6,6};{10, 9, 8, 7, 6,5,4,3,2,1};{1, 999, 13, 666, -5};{    1  ,  3  ,  5 , 7,9 };";
    char * start = str;
    for (char * cur = start; *cur != '\0'; cur++) {
        if(*cur == ';') {
            *cur = '\0';
            printf("test -- %s --\n", start);
            assert(test(start) == 1);
            start = cur+1;
        }
    }
}

void test_invalid() {
    char str []= "{,1};{1,};{- 1};{{a,b,c};{01};{ 00,1,2,3 };{ a, b, c };{1, 2.0, 3};{1, 999, 13, 666, - 5};{1, {2,3}, 4};{1, 2, 3, 4, five};{ 1 2 3 4 };{1,2,3,,,,5};{0,1,4,2,4}};{0, 00 ,0.0};";
    char * start = str;
    for (char * cur = start; *cur != '\0'; cur++) {
        if(*cur == ';') {
            *cur = '\0';
            printf("\ntest -- %s --\n", start);
            assert(test(start) == 0);
            start = cur+1;
        }
    }
}

int test(char *str) {
    struct Intset set;
    int res = parser_intset(str, &set);
    if (res) { print_intset(&set); }
    free(set.vals);
    return res;
}

void newIntset(Intset* s, int c) {
    if (s == NULL) return;
    s->size = 0;
    s->capacity = c;
    s->vals = calloc(sizeof(int), s->capacity);
}

void newSlices(Slices* s, int c) {
    s->size = 0;
    s->capacity = c;
    s->pieces = calloc(sizeof(char*), s->capacity);
}

int cmpfunc (const void * a, const void * b) {
    return ( *(int*)a - *(int*)b );
}

int contains(int n, Intset * s) {
    if (s == NULL) return 0;
    if (s->size == 0) return 0;
    int b = 0;
    int e = s->size-1;
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

void addToSet(Intset * s, int n) {
    if (s == NULL) return;
    if (s->size == s->capacity) {
        s->capacity *= 2;
        s->vals = realloc(s->vals, sizeof(int) * s->capacity);
    }
    if (contains(n, s)) {
        return;
    }
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
    if (str[0] != '{') return 0;
    if (str[len-1] != '}') return 0;
    newSlices(&slice, len);

    split(&slice, str);
    if (slice.size == 1 && strlen(slice.pieces[0]) == 0) {
        newIntset(out, slice.size);
    } else {
        const char * errstr;
        newIntset(out, slice.size);
        for(i = 0; i < slice.size; i ++) {
            int num = strtonum(slice.pieces[i], INT_MIN, INT_MAX, &errstr);
            if (errstr) {
                printf(" %s| %s error flag \n", slice.pieces[i], errstr);
                free(slice.pieces);
                return 0;
            } else {
                addToSet(out, num);
            }
        }
    }
    free(slice.pieces);
    return 1;
}

char * intset_convert(Intset * set) {
    int i;
    char tmp[14];
    char * str = malloc(sizeof(char) * 14 * set->size + 3);
    if (set == NULL) { return NULL; }
    if (set->size == 0 ) {
        strcpy(str, "{}");
    } else if (set->size == 1) {
        sprintf(str, "{%d}", set->vals[0]);
    } else {
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
    str = realloc(str, strlen(str)+1);
    return str;
}

int is_equal(Intset * a, Intset * b) {
    int res = 0;
    if (a != NULL && b != NULL && a->size == b->size) {
        char * stra = intset_convert(a);
        char * strb = intset_convert(b);
        if (!strcmp(stra, strb)){
            res = 1;
        }
        free(stra);
        free(strb);
    }
    return res;
}

int is_subset(Intset * a, Intset * b) {
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
            if (i == a->size) res = 1;
        }
    }
    return res;
}

Intset * intset_union(Intset *a, Intset* b) {
    Intset * result = NULL;

    if (a != NULL && b != NULL) {
        int i;
        result = malloc(sizeof(struct Intset));
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
                    l++; //**required
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
            int * set;
            set = (a->size == 0)? b->vals : a->vals;
            result->size = (a->size == 0)? b->size : a->size;
            for(i = 0; i < result->size; i++) result->vals[i] = set[i];
        }
    }
    return result;
}

Intset * intset_intersect(Intset *a, Intset* b) {
    Intset * result = NULL;
    if (a != NULL && b != NULL) {
        result = malloc(sizeof(struct Intset));
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

Intset * intset_disjunction(Intset *a, Intset* b) {
    Intset * result = NULL;
    if (a != NULL && b != NULL) {
        if (a->size == 0 || b->size == 0){
            result = intset_union(a, b);
        } else {
            int i, l=0, r=0;
            result = malloc(sizeof(struct Intset));
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
    return result;
}

Intset * intset_difference(Intset *a, Intset* b) {
    Intset * result = NULL;
    if (a != NULL && b != NULL) {
        if (a->size == 0) {
            result = intset_intersect(a, b);
        } else if (b->size == 0){
            result = intset_union(a, b);
        } else {
            int i;
            result = malloc(sizeof(struct Intset));
            newIntset(result, (a->size));
            for(i = 0; i < a->size; i++) {
                if(!contains(a->vals[i],b))
                    addToSet(result, a->vals[i]);
            }
        }
    }
    return result;
}

void union_test(Intset *a, Intset*b, Intset*e){
    print_intset(a);
    printf("v ");
    print_intset(b);
    Intset * r = intset_union(a, b);
    printf("--result-------------------\n");
    print_intset(r);
    assert(is_equal(r,e) == 1);
    assert(r->size==e->size);
    free(r->vals);
    free(r);
}

void disjunction_test(Intset *a, Intset*b, Intset*e){
    print_intset(a);
    printf("xor ");
    print_intset(b);
    Intset * r = intset_disjunction(a, b);
    printf("--result-------------------\n");
    print_intset(r);
    assert(is_equal(r,e) == 1);
    assert(r->size==e->size);
    free(r->vals);
    free(r);
}

void intersect_test(Intset *a, Intset*b, Intset*e){
    print_intset(a);
    printf("^ ");
    print_intset(b);
    Intset * r = intset_intersect(a, b);
    printf("--result-------------------\n");
    print_intset(r);
    assert(is_equal(r,e) == 1);
    assert(r->size==e->size);
    free(r->vals);
    free(r);
}

void difference_test(Intset *a, Intset*b, Intset*e){
    print_intset(a);
    printf("- ");
    print_intset(b);
    Intset * r = intset_difference(a, b);
    printf("--result-------------------\n");
    print_intset(r);
    assert(is_equal(r,e) == 1);
    assert(r->size==e->size);
    free(r->vals);
    free(r);
}

struct Intset  a, b, c, d, e,f,g;
char stra[] = "{ }";
char strb[] = "{2,3,1,5}";
char strc[] = "{2,2,2,2,2,3,2}";
char strd[] = "{1,5}";
char stre[] = "{1,2,5}";
char strf[] = "{2}";
char strg[] = "{6}";

void test_difference(){
    //also test isequal
    printf("\n###test difference empty set\n");
    difference_test(&a, &b, &a);

    printf("\n###test difference empty set\n");
    difference_test(&b, &a, &b);

    printf("\n###test difference subset set\n");
    difference_test(&b, &c, &d);

    printf("\n###test difference disjunction sets\n");
    difference_test(&d, &c, &d);

    printf("\n###test difference overlap sets\n");
    difference_test(&e, &c, &d);
}


void test_union(){
    //also test isequal
    printf("###test union empty set\n");
    union_test(&a, &b, &b);
    assert(is_subset(&c,&b) == 1);

    printf("\n###test union subset set\n");
    union_test(&b, &c, &b);
    assert(is_subset(&c,&b) == 1);

    printf("\n###test union disjunction sets\n");
    union_test(&d, &c, &b);
    assert(is_subset(&d,&c) == 0);

    printf("\n###test union overlap sets\n");
    union_test(&e, &c, &b);
    assert(is_subset(&e,&c) == 0);
}

void test_intersect(){
    printf("\n###test intersect empty set\n");
    intersect_test(&a, &b, &a);

    printf("\n###test intersect subset set\n");
    intersect_test(&b, &c, &c);

    printf("\n###test intersect subset set\n");
    intersect_test(&b, &d, &d);

    printf("\n###test intersect subset set\n");
    intersect_test(&b, &e, &e);

    printf("\n###test intersect disjunction sets\n");
    intersect_test(&d, &c, &a);

    printf("\n###test intersect disjunction sets\n");
    intersect_test(&g, &d, &a);


    printf("\n###test intersect overlap sets\n");
    intersect_test(&e, &c, &f);
}

void test_disjunction(){

    printf("\n###test disjunction empty set\n");
    disjunction_test(&a, &b, &b);

    printf("\n###test disjunction subset set\n");
    disjunction_test(&b, &c, &d);

    printf("\n###test disjunction subset set\n");
    disjunction_test(&b, &d, &c);

    printf("\n###test disjunction subset set\n");
    disjunction_test(&e, &d, &f);


    printf("\n###test  disjunction disjuct  sets\n");
    disjunction_test(&d, &c, &b);

    printf("\n###test  disjunction disjunct sets\n");
    Intset * k = intset_union(&g,&d);
    disjunction_test(&g, &d, k);
    free(k->vals);
    free(k);
}

void test_operation(){
    parser_intset(stra, &a);
    parser_intset(strb, &b);
    parser_intset(strc, &c);
    parser_intset(strd, &d);
    parser_intset(stre, &e);
    parser_intset(strf, &f);
    parser_intset(strg, &g);

    test_union();

    test_intersect();

    test_disjunction();

    test_difference();

    free(a.vals);
    free(b.vals);
    free(c.vals);
    free(d.vals);
    free(e.vals);
    free(f.vals);
    free(g.vals);

}

int main(int argc, char *argv[]) {
    test_invalid();
    test_valid();
    test_contains();
    test_operation();

    //
    //    struct Intset  a;
    //    char stra[] = "{1,3,1,3,1}";
    //    int res = parser_intset(stra, &a);
    //    if (res) print_intset(&a);
    //    free(a.vals);
    return EXIT_SUCCESS;
}
