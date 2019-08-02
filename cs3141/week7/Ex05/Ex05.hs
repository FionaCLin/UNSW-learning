{-# LANGUAGE FlexibleContexts #-}
module Ex05 where
import Text.Read (readMaybe)
import System.IO
import Data.Char
import System.Environment
import Control.Monad.State
import System.Random
import Test.QuickCheck

capitalise :: FilePath -> FilePath -> IO ()
capitalise i o = do
  str <- readFile i
  writeFile o $ map toUpper str

  
sumFile :: IO ()
sumFile = do
  [iFile, oFile] <- getArgs 
  str <- readFile iFile
  let listWords = words str -- split lines of words
  let listNums =  map (read::String->Int) listWords
  let total = sum listNums -- sum the list of numbers
  let outputStr = show total -- convert number to string
  writeFile oFile outputStr


data Player m = Player { guess :: m Int
                       , wrong :: Answer -> m ()
                       }
data Answer = Lower | Higher

guessingGame :: (Monad m) => Int -> Int -> Player m -> m Bool
guessingGame x n p = go n
  where
   go 0 = pure False
   go n = do
      x' <- guess p
      case compare x x' of
       LT -> wrong p Lower  >> go (n-1)
       GT -> wrong p Higher >> go (n-1)
       EQ -> pure True

human :: Player IO
human = Player { guess = guess, wrong = wrong }
  where
    guess = do
      putStrLn "Enter a number (1-100):"
      x <- getLine
      case readMaybe x of
        Nothing -> guess
        Just i  -> pure i

    wrong Lower  = putStrLn "Lower!"
    wrong Higher = putStrLn "Higher!"

play :: IO ()
play = do
  x <- randomRIO (1,100)
  putStrLn (show x)
  b <- guessingGame x 5 human
  putStrLn (if b then "You got it!" else "You ran out of guesses!")


midpoint :: Int -> Int -> Int
midpoint lo hi | lo <= hi  = lo + div (hi - lo) 2
               | otherwise = midpoint hi lo


ai :: Player (State (Int,Int))
ai = Player { guess = guess, wrong = wrong }
  where 
    guess = do 
      s <- get
      let lo = fst s 
      let hi = snd s
      pure $ midpoint lo hi

    wrong Lower  = do 
      s <- get
      let lo = fst s 
      let hi = snd s
      put (lo, midpoint lo hi - 1)
    wrong Higher = do 
      s <- get
      let lo = fst s 
      let hi = snd s
      put ( midpoint lo hi + 1 , hi )


prop_basic (Positive n) = forAll (choose (1,n)) $ \x -> evalState (guessingGame x n ai) (1,n)

prop_optimality (Positive n) = forAll (choose (1,n)) $ \x -> evalState (guessingGame x (bound n) ai) (1,n)
  where bound n = ceiling (logBase 2 (fromIntegral n)) + 1


