module Ex02 where
import Test.QuickCheck
import Data.List
import Debug.Trace (traceShow)
-- implement the following functions, which meet some (but not all!) of the 
-- properties of a correct sorting function

-- prop2 & 4, but not prop1 & 3 & 5
dodgySort1 :: [Int] -> [Int]
dodgySort1 xs = xs


-- prop1 & 2 & 3, but not prop4 & 5
dodgySort2 :: [Int] -> [Int]
dodgySort2 xs = foldr dodgySort2 [] xs
  where 
    dodgySort2 x [] = [x]
    dodgySort2 x (y : ys) 
      | x  < y = x : y : ys
      | x  == y = x : ys
      | otherwise = y : dodgySort2 x ys


-- prop1 & 3 & 4, but not prop2 & 5
dodgySort3 :: [Int] -> [Int]
dodgySort3 xs = let ys = [k*2 | k <- xs]
  in foldr dodgySort3 [] ys
  where 
    dodgySort3 x [] = [x]
    dodgySort3 x (y : ys) 
      | x  < y = x : y : ys
      | otherwise = y : dodgySort3 x ys
 
            
-- prop1 & 2 & 3 & 4, but not prop5
dodgySort4 :: [Int] -> [Int]
dodgySort4 xs = foldr flat [] (foldr dodgySort [] xs)
  where 
      flat x [] = [x]
      flat x (y : ys) 
        | x  < y = x : y : ys
        | x == y = x : y+1 : ys
        | otherwise = y : flat x ys

      dodgySort x [] = [x]
      dodgySort x (y : ys) 
        | x  < y = x : y : ys
        | otherwise = y : dodgySort x ys

  
-- Properties of sorting function   
-- Prop 1 reverse xs and xs has the same elements x, after sortFn, 2*x, x-constants are in outpur list  
sortProp1 :: ([Int] -> [Int]) -> [Int] -> Bool
sortProp1 sortFn xs = sortFn xs == sortFn (reverse xs)

-- Prop 2 xs has its elements x, after sortFn, 2*x, x-constants are in output list
--        then x no longer exists.
sortProp2 :: ([Int] -> [Int]) -> Int -> [Int] -> [Int] -> Bool
sortProp2 sortFn x xs ys = x `elem` sortFn (xs ++ [x] ++ ys)


-- Prop 3 refine this sortFn is sorting in ascending order
sortProp3 :: ([Int] -> [Int]) -> [Int] -> Bool
sortProp3 sortFn xs = isSorted (sortFn xs)
  where 
    isSorted (x1 : x2 : xs) = (x1 <= x2) && isSorted (x2 : xs)
    isSorted _ = True

-- Prop4 input list and output list has the same length, which mean keep the duplicated one
sortProp4 :: ([Int] -> [Int]) -> [Int] -> Bool    
sortProp4 sortFn xs = length xs == length (sortFn xs)

-- Prop5
sortProp5 :: ([Int] -> [Int]) -> [Int] -> Bool
sortProp5 sortFn xs 
  = sortFn xs == insertionSort xs

insertionSort :: [Int] -> [Int]
insertionSort xs = foldr insertSorted [] xs
  where 
    insertSorted x [] = [x]
    insertSorted x (y : ys) 
      | x <= y = x : y : ys
      | otherwise = y : insertSorted x ys

