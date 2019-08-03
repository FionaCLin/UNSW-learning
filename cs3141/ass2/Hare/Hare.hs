{-# LANGUAGE GADTs, DataKinds, KindSignatures, TupleSections, PolyKinds, TypeOperators, TypeFamilies, PartialTypeSignatures #-}
module Hare where
import Control.Monad
import Control.Applicative
import HareMonad

data RE :: * -> * where
  Empty :: RE ()
  Fail :: RE a
  Char :: [Char] -> RE Char
  Seq :: RE a -> RE b -> RE (a, b) -- Untyped.hs match return tuple at the end
  Choose :: RE a -> RE a -> RE a -- Untyped.hs match return match a <|> match b at the end
  Star :: RE a -> RE [a]
  Action :: (a -> b) -> RE a -> RE b


match :: (Alternative f, Monad f) => RE a -> Hare f a
match (Empty) = pure ()
match (Fail) = empty
match (Char xs) = do
    x <- readCharacter
    guard (x `elem` xs)
    pure x

match (Seq a b) = do
    ra <- match a
    rb <- match b
    let tup = (ra, rb)
    pure tup

match (Choose a b) = do
  selection <- match a <|> match b
  pure selection

match (Star a) = (:) <$> match a <*> match (Star a) <|> pure []

match (Action f a) = fmap f (match a)

matchAnywhere :: (Alternative f, Monad f) => RE a -> Hare f a
matchAnywhere re = match re <|> (readCharacter >> matchAnywhere re)

(=~) :: (Alternative f, Monad f) => String -> RE a -> f a
(=~) = flip (hare . matchAnywhere)

infixr `cons`
cons :: RE a -> RE [a] -> RE [a]
cons x xs = Action(\t -> (fst t):(snd t)) (Seq x xs)

string :: String -> RE String
string (x:xs) = cons (Char [x]) (string xs)
string _ = Action(\x -> []) Empty

rpt :: Int -> RE a -> RE [a]
rpt n re
  |n > 0 = cons re (rpt (n-1) re)
  |otherwise = Action (\x -> []) Empty

rptRange :: (Int, Int) -> RE a -> RE [a]
rptRange (x,y) re = choose (map (\n -> rpt n re) [y, y-1..x])

option :: RE a -> RE (Maybe a)
option re = Choose (Action(\x-> Just x) re) (Action(\x -> Nothing) Empty)

plus :: RE a -> RE [a]
plus re = Action(\t -> (fst t):(snd t)) (Seq re (Star re))


choose :: [RE a] -> RE a
choose (r:re) = Choose r (choose re)
choose _ = Fail

