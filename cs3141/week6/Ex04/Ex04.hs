module Ex04 where

import Text.Read (readMaybe)

data Token = Number Int | Operator (Int -> Int -> Int)  

parseToken :: String -> Maybe Token
parseToken "+" = Just (Operator (+))
parseToken "-" = Just (Operator (-))
parseToken "/" = Just (Operator div)
parseToken "*" = Just (Operator (*))
parseToken str = fmap Number (readMaybe str)

tokenise :: String -> Maybe [Token]
tokenise = mapM parseToken <$> words

newtype Calc a = C ([Int] -> Maybe ([Int], a))

pop :: Calc Int
pop = C doPop
  where 
    doPop :: [a] -> Maybe ([a], a)
    doPop []      = Nothing 
    doPop xs = Just (init xs, last xs)

push :: Int -> Calc ()
push i = C (\xs -> Just(xs++[i], ()))


instance Functor Calc where
  fmap f (C sa) = C $ \s ->
      case sa s of 
        Nothing      -> Nothing
        Just (s', a) -> Just (s', f a)

instance Applicative Calc where
  pure x = C (\s -> Just (s,x))
  C sf <*> C sx = C $ \s -> 
      case sf s of 
          Nothing     -> Nothing
          Just (s',f) -> case sx s' of
              Nothing      -> Nothing
              Just (s'',x) -> Just (s'', f x)

instance Monad Calc where
  return = pure
  C sa >>= f = C $ \s -> 
      case sa s of 
          Nothing     -> Nothing
          Just (s',a) -> unwrapCalc (f a) s'
    where unwrapCalc (C a) = a


evaluate :: [Token] -> Calc Int
evaluate xs = doEvaluate xs >> pop
  where 
    doEvaluate :: [Token] -> Calc Int
    doEvaluate [] = pure 0 
    doEvaluate (Number i:ts) = push i >> doEvaluate ts  
    doEvaluate (Operator o:ts) = do 
      y <- pop
      x <- pop 
      push (o x y) >> doEvaluate ts

getRes :: Calc Int -> Maybe ([Int], Int)
getRes c = unwrapCalc c []
  where unwrapCalc (C a) = a

nextOne :: Maybe (Maybe ([Int], Int)) -> Maybe Int
nextOne (Just (Just(_, i))) = Just i
nextOne _ = Nothing 

calculate :: String -> Maybe Int
calculate s = nextOne $ getRes . evaluate <$> tokenise s
