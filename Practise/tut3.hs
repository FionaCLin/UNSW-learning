fiona@lin:~/UNSW-learning/Practise$ ghci
GHCi, version 7.10.3: http://www.haskell.org/ghc/  :? for help
Prelude>  :t [1,2]
[1,2] :: Num t => [t]
Prelude> :t [1,2,3,4]
[1,2,3,4] :: Num t => [t]
Prelude>  :t (1,2)
(1,2) :: (Num t, Num t1) => (t, t1)
Prelude> :t (1,2,3,4)
(1,2,3,4) :: (Num t, Num t1, Num t2, Num t3) => (t, t1, t2, t3)
Prelude> [1 ,"two"]

<interactive>:6:2:
    No instance for (Num [Char]) arising from the literal ‘1’    In the expression: 1    In the expression: [1, "two"]
    In an equation for ‘it’: it = [1, "two"]
Prelude> (1 ,"two")
(1,"two")
Prelude> [1,2] == [1,2,3,4]
False
Prelude> (1,2) == (1,2,3,4)

<interactive>:9:10:
    Couldn't match expected type ‘(Integer, Integer)’
                with actual type ‘(Integer, Integer, Integer, Integer)’
    In the second argument of ‘(==)’, namely ‘(1, 2, 3, 4)’
    In the expression: (1, 2) == (1, 2, 3, 4)
    In an equation for ‘it’: it = (1, 2) == (1, 2, 3, 4)
Prelude> let x = ("Honda", "Civic", 2003)
Prelude> fst x

<interactive>:11:5:
    Couldn't match expected type ‘(a, b0)’
                with actual type ‘([Char], [Char], Integer)’
    Relevant bindings include it :: a (bound at <interactive>:11:1)
    In the first argument of ‘fst’, namely ‘x’
    In the expression: fst x
Prelude> x
("Honda","Civic",2003)
Prelude> let x = ("Honda", "Civic")
Prelude> fst x
"Honda"
Prelude> snd x
"Civic"
Prelude> zip x ["Fred", "Ginger"]

<interactive>:16:5:
    Couldn't match expected type ‘[a]’
                with actual type ‘([Char], [Char])’
    Relevant bindings include
      it :: [(a, [Char])] (bound at <interactive>:16:1)
    In the first argument of ‘zip’, namely ‘x’
    In the expression: zip x ["Fred", "Ginger"]
Prelude> x
("Honda","Civic")
Prelude> zip ["Adam", "Caleb"] ["Fred", "Ginger"]
[("Adam","Fred"),("Caleb","Ginger")]
Prelude> let nums = [1..8]
Prelude> let words = ["one", "two", "three", "four", "five", "six", "seven", "eight"]
Prelude> let pair = zip nums words
Prelude> pair
[(1,"one"),(2,"two"),(3,"three"),(4,"four"),(5,"five"),(6,"six"),(7,"seven"),(8,"eight")]
Prelude> [(fst p, fst q) | p <- pairs, q <- pairs]

<interactive>:23:24:
    Not in scope: ‘pairs’
    Perhaps you meant ‘pair’ (line 21)

<interactive>:23:36:
    Not in scope: ‘pairs’
    Perhaps you meant ‘pair’ (line 21)
Prelude> [(fst p, fst q) | p <- pair, q <- pair]
[(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(3,1),(3,2),(3,3),(3,4),(3,5),(3,6),(3,7),(3,8),(4,1),(4,2),(4,3),(4,4),(4,5),(4,6),(4,7),(4,8),(5,1),(5,2),(5,3),(5,4),(5,5),(5,6),(5,7),(5,8),(6,1),(6,2),(6,3),(6,4),(6,5),(6,6),(6,7),(6,8),(7,1),(7,2),(7,3),(7,4),(7,5),(7,6),(7,7),(7,8),(8,1),(8,2),(8,3),(8,4),(8,5),(8,6),(8,7),(8,8)]
Prelude> [(fst p, fst q) | p <- pair, q <- pair, fst p < fst q]
[(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(3,4),(3,5),(3,6),(3,7),(3,8),(4,5),(4,6),(4,7),(4,8),(5,6),(5,7),(5,8),(6,7),(6,8),(7,8)]
