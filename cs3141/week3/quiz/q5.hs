import Data.List
import Test.QuickCheck

foo :: [a] -> (a -> b) -> [b]
-- foo = undefined -- see below
-- foo xs f = []  --  option 1 x
-- foo xs f = xs -- option 2
-- option 3 --
-- foo [] f = []
-- foo (x:xs) f = x : foo xs f
-- option 4 --
foo [] f = []
foo (x:xs) f = f x : foo xs f
-- option 5 --
-- foo [] f = []
-- foo (x:xs) f = foo xs f

prop_1 :: [Int] -> Bool
prop_1 xs = foo xs id == xs 

prop_2 :: [Int] -> (Int -> Int) -> (Int -> Int) -> Bool
prop_2 xs f g = foo (foo xs f) g == foo xs (g . f)
