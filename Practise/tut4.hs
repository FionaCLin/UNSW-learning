-- GHCi, version 7.10.3: http://www.haskell.org/ghc/  :? for help
-- Prelude>  :t Tr
-- Traversable  True
-- Prelude>  :t True
-- True :: Bool
-- Prelude> :t 'a'
-- 'a' :: Char
-- Prelude> :t "Hello"
-- "Hello" :: [Char]
-- Prelude> :t 3
-- 3 :: Num a => a
-- Prelude> let x = 3
-- Prelude> :t x
-- x :: Num a => a
-- Prelude> 3 :: Int
-- 3
-- Prelude> 3 :: Double
-- 3.0
-- Prelude> :t head
-- head :: [a] -> a
-- Prelude> :t (+)
-- (+) :: Num a => a -> a -> a
-- Prelude> :t zip
-- zip :: [a] -> [b] -> [(a, b)]
-- Prelude>
-- Prelude>
-- Leaving GHCi.

f :: [Int] -> Int
f ls = head ls + length ls


dividesEvenly :: Int -> Int -> Bool
dividesEvenly x y = (y `div` x) * x == y

-- fiona@lin:~/UNSW-learning/Practise$ ghci
-- GHCi, version 7.10.3: http://www.haskell.org/ghc/  :? for help
-- Prelude> :l tut4.hs
-- [1 of 1] Compiling Main             ( tut4.hs, interpreted )
-- Ok, modules loaded: Main.
-- *Main> dividesEvenly 5 2
-- False
-- *Main> dividesEvenly  2 5
-- False
-- *Main>
