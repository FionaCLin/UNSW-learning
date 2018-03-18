#include <stdio.h>
#include <stdlib.h>

#include "bfs.h"

void barf (char *err) {
  fprintf (stderr, "ERROR: %s\n", err);
  exit(EXIT_FAILURE);
}

void nag (char *err) {
  fprintf (stderr, "WARNING: %s\n", err);
}

RetVal cmpKey(Key k1, Key k2) {
  return k1 == k2 ? Success : Failure;
}

RetVal readKey(Key *k) {
  return 1==scanf("%u", k) ? Success : Failure;
}

RetVal readKeyList(int *ll, Key l[], int N) {
  int i;
  if (1 != scanf("%d", ll) || *ll < 0 || *ll >= N)
    return Failure;
  for (i = 0; i < *ll; i++)
    if (Failure == readKey(l+i))
      return Failure;
  return Success;
}

RetVal readT(T *t) {
  return 1==scanf("%f", t) ? Success : Failure;
}

void printKey(Key k) {
  printf("%u", k);
}

void printT(T t) {
  printf("%f", t);
}

/* helper function to make a new tiny tree */
RetVal tinserthere (Key id, T val, Tree *t) {
  if (*t == NULL) {
    if((*t = malloc (sizeof (struct node))) == NULL) {
      fprintf (stderr, "Error: malloc failed in tinserthere!\n");
      return Failure;
    }
    (*t)->id = id;
    (*t)->val = val;
    (*t)->list = NULL;
    return Success;
  } else
    return Failure;
}

/* helper function only used in tinsert to prepend a new tree node to a list */
RetVal ninserthere (Key id, T val, List *l) {
  List here;
  if((here = malloc (sizeof (struct lnode))) == NULL) {
    nag ("malloc failed in ninserthere");
    return Failure;
  }
  here->next = *l;
  *l = here;
  here->n = NULL;
  if (Success == tinserthere(id, val, &((*l)->n)))
    return Success;
  else {
    free(here);
    return Failure;
  }
}

RetVal ninsert (Key id, T val, int ll, Key l[], List *list) {
  if (ll == 0)
    return ninserthere(id, val, list);
  else {
    List h;
    for (h = *list; h != NULL; h = h->next)
      if (cmpKey(l[0], h->n->id) == Success)
        return ninsert (id, val, ll-1, l+1, &(h->n->list));
    return Failure;
  }
}
    
RetVal tinsert (Key id, T val, int ll, Key l[], Tree *t) {
  if (ll == 0)
    return tinserthere(id, val, t);
  else if (*t != NULL && Success == cmpKey((*t)->id, l[0]))
    return ninsert(id, val, ll-1, l+1, &((*t)->list));
  else
    return Failure;
}

/* 2 helper functions for printing trees */
void printi(int i) {
  while(i-- > 0)
    printf("  ");
}

void tprinti(Tree t,int i) {
  if (t != NULL) {
    List l = t->list;
    printi(i); printKey(t->id); printf(": "); printT(t->val); printf(",\n");
    printi(i); printf("[\n");
    while (l != NULL) {
      tprinti(l->n,i+1);
      l = l->next;
    }
    printi(i); printf("]\n");
  }
}

void tprint(Tree t) {
  tprinti(t,0);
}


