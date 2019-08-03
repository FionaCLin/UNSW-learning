increasing :: (Ord a) => [a] -> Bool

-- increasing xs = if xs == []
--                 then True
--                 else if tail xs == []
--                       then True
--                       else if head xs <= head(tail xs)
--                             then increasing (tail xs)
--                             else False
increasing (x:y:ys) = x <= y && increasing(y:ys)
increasing _ = True


-- noVowels :: [Char] -> [Char]
-- noVowels word = if word == ""
--                   then ""
--                   else if head word `elem` "aeiouAEIOU"
--                         then noVowels (tail word)
--                         else (head word) : noVowels (tail word)
-- Prelude> :l tut5.hs
-- [1 of 1] Compiling Main             ( tut5.hs, interpreted )

-- tut5.hs:10:1: Warning:
--     Pattern match(es) are overlapped
--     In an equation for ‘increasing’: increasing (x : y : ys) = ...
-- Ok, modules loaded: Main.
-- *Main> :l tut5.hs
-- [1 of 1] Compiling Main             ( tut5.hs, interpreted )
-- Ok, modules loaded: Main.
-- *Main> :l tut5.hs
-- [1 of 1] Compiling Main             ( tut5.hs, interpreted )
-- Ok, modules loaded: Main.
-- *Main> noVowels "The quick brown fox jumps over the lazy dog."
-- "Th qck brwn fx jmps vr th lzy dg."

noVowels :: [Char] -> [Char]
noVowels "" = ""
noVowels (x:xs) 
        | x `elem` "aeiouAEIOU" = noVowels xs
        | otherwise             = x : noVowels xs


-- watch :: Int -> [Char]
-- watch n = if n == 7
--             then "7 o'clock and ... SHARKNADO!"
--             else show n ++ " o'clock and all's well."

-- --- pattern matching
-- watch 7 = "7 o'clock and ... SHARKNADO!"
-- watch n = show n ++ " o'clock and all's well."

-- WHERE EXPRESSION
-- watch n = show n ++ " o'clock and " ++ message n  
--           where message 7 = "... SHARKNADO!"
--                 message _ = "all's well."

-- case EXPRESSION
watch n = show n ++ " o'clock and " ++ case n of 7 -> "... SHARKNADO!"
                                                 _ -> "all's well."


gravity :: (Fractional a) => a -> a
-- gravity r =  6.674e-11 * 5.972e24 / (r ^ 2)                                               

gravity r = let g = 6.674e-11
                earthMass = 5.972e24
            in g * earthMass / (r ^ 2)                                               
-- pattern note :
-- exhausted rules
-- pattern = result
-- ...

-- pattern 
--   | expression = result
--     ...
--   | otherwise = result

-- result where 
--   pattern = result
--   ...

-- let pattern = result
--   ...
-- in result

-- case expression of pattern -> result
--                   ...

-- use same level of indentation
