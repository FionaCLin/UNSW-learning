#ifndef _dict_h
#define _dict_h

#define VECSIZE ('z'-'a' + 1)

/* words and characteristic character vectors */
typedef char *word;
enum __bool__ { FALSE, TRUE };
typedef enum __bool__ bool;

/* tries */
typedef struct __tnode__  *Dict, TNode;

/* tries with letters and end markers */
struct __tnode__ {
  Dict cvec[VECSIZE];           /* what is this a prefix of? */
  bool eow;                     /* is this the end of the wor(l)d? */
};

void newdict(Dict *dp);
void addword (const Dict r, const word w);
bool checkword (const Dict r, const word w);
void delword (const Dict r, const word w);

void barf(char *s);             /* send s to stderr and die */

#endif
