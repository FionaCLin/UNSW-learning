*Main> [2^n | n <-[1..10]]
[2,4,8,16,32,64,128,256,512,1024]
*Main> [2^n | n <-[1..10], 2^n >= 10, 2^n < 100]
[16,32,64]
*Main> [x | x <- "outrageous", not (elem x "aeiou)]

<interactive>:41:45:
    lexical error in string/character literal at end of input
*Main> [x | x <- "outrageous", not (elem x "aeiou")]
"trgs"
*Main> [x | x <- "outrageous", not (x `elem` "aeiou")]
"trgs"
*Main> [x | x <- "outrageous", not (x `elem` "aeiou") | word < ["bell", "book", "candle"]]

<interactive>:44:6:
    Unexpected parallel statement in a list comprehension
    Use ParallelListComp

<interactive>:44:50:
    Not in scope: ‘word’
    Perhaps you meant ‘words’ (imported from Prelude)
*Main> [x | x <- wword, not (x `elem` "aeiou") | word < ["bell", "book", "candle"]]
<interactive>:45:6:
    Unexpected parallel statement in a list comprehension
    Use ParallelListComp

<interactive>:45:11: Not in scope: ‘wword’

<interactive>:45:43:
    Not in scope: ‘word’
    Perhaps you meant ‘words’ (imported from Prelude)
*Main> [x | x <- word, not (x `elem` "aeiou") | word < ["bell", "book", "candle"]]

<interactive>:46:6:
    Unexpected parallel statement in a list comprehension
    Use ParallelListComp

<interactive>:46:11:
    Not in scope: ‘word’
    Perhaps you meant ‘words’ (imported from Prelude)

<interactive>:46:42:
    Not in scope: ‘word’
    Perhaps you meant ‘words’ (imported from Prelude)
*Main> [x | x <- word, not (x `elem` "aeiou") | word <- ["bell", "book", "candle"]]

<interactive>:47:6:
    Unexpected parallel statement in a list comprehension
    Use ParallelListComp

<interactive>:47:11:
    Not in scope: ‘word’
    Perhaps you meant ‘words’ (imported from Prelude)
*Main> [x | x <- word, not (x `elem` "aeiou") | word <- ["bell", "book", "candle"]]

<interactive>:48:6:
    Unexpected parallel statement in a list comprehension
    Use ParallelListComp

<interactive>:48:11:
    Not in scope: ‘word’
    Perhaps you meant ‘words’ (imported from Prelude)
*Main> [x | x <- word, not (x `elem` "aeiou") ]| word <- ["bell", "book", "candle"]]

<interactive>:49:41: parse error on input ‘|’
*Main> [[x | x <- word, not (x `elem` "aeiou")]| word <- ["bell", "book", "candle"]]
["bll","bk","cndl"]
*Main> [[x*y | y <- [1..9] | x <- [1..9]]

<interactive>:51:35:
    parse error (possibly incorrect indentation or mismatched brackets)
*Main> [[x*y | y <- [1..9]]| x <- [1..9]]
[[1,2,3,4,5,6,7,8,9],[2,4,6,8,10,12,14,16,18],[3,6,9,12,15,18,21,24,27],[4,8,12,16,20,24,28,32,36],[5,10,15,20,25,30,35,40,45],[6,12,18,24,30,36,42,48,54],[7,14,21,28,35,42,49,56,63],[8,16,24,32,40,48,56,64,72],[9,18,27,36,45,54,63,72,81]]
*Main>
