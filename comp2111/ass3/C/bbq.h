#ifndef bbq_h
#define bbq_h

/* descriptive names for 0 and 1 as return values */
typedef enum {Failure, Success} RetVal;

/* key and payload types */
typedef float T;
typedef unsigned int Key;

/* tree nodes */
struct lnode;
typedef struct node {
  Key id;                       /* key */
  T val;                        /* payload */
  struct lnode *list;           /* successor list */
} *Tree;

/* list nodes */
typedef struct lnode {
  Tree n;
  struct lnode *next;
} *List;

/* catastrophic errors */
void barf (char *err);
/* recoverable issues */
void nag (char *err);

/* convenience for keys and payloads */
RetVal cmpKey(Key k1, Key k2);

RetVal readKey(Key *k);
RetVal readT(T *t);
RetVal readKeyList(int *ll, Key l[], int N);

void printKey(Key k);
void printT(T t);

/* these are provided so that the test harness can build and display trees */
RetVal tinsert (Key id, T val, int ll, Key l[], Tree *t);
void tprint(Tree t);

/* see spec in ass3.pdf */
void search (Tree root, unsigned int N, Key key, T *val, RetVal *found);

#endif /* bbq_h */
