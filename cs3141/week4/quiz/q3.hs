import Data.List
import Test.QuickCheck

type Graph = [[Bool]]

g :: Graph
g = [[False, True,  True,  False],
     [True,  False, False, True ],
     [True,  False, False, True ],
     [False, True,  True,  False]]

prop_1 :: Graph -> Bool
prop_1 g = transpose g == g
prop_2 :: Graph -> Bool
prop_2 g = all or g
prop_3 :: Graph -> Bool
prop_3 g = all (\x -> length x == length g) g

prop_4:: Graph -> Bool
prop_4 g = map reverse g == g
