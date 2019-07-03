module TortoiseCombinators
       ( andThen 
       , loop 
       , invisibly 
       , retrace 
       , overlay 
       ) where

import Tortoise

-- See Tests.hs or the assignment spec for specifications for each
-- of these combinators.
-- sequential composition (4 marks)

andThen :: Instructions -> Instructions -> Instructions
andThen Stop i1 = i1
andThen i2 Stop = i2
andThen (Move distance i1) i2 = Move distance $ i1 `andThen` i2
andThen (Turn angle i1) i2 = Turn angle $ i1 `andThen` i2
andThen (SetStyle linestyle i1) i2 = SetStyle linestyle $ i1 `andThen` i2
andThen (SetColour colour i1) i2 = SetColour colour $ i1 `andThen` i2
andThen (PenDown i1) i2 = PenDown $ i1 `andThen` i2
andThen (PenUp i1) i2 = PenUp $ i1 `andThen` i2

-- Bounded Looping (4 marks)
loop :: Int -> Instructions -> Instructions
loop n i
  | (n <= 0) = Stop
  | otherwise = i `andThen` (loop (n-1) i)
  
-- Invisibility (4 marks)
drawing:: Bool -> Instructions -> Instructions
-- if pen is up then we PenUp is True from last step
-- so final state correctly has isUp to Stop too
drawing isUp Stop  = if isUp then Stop else PenDown Stop
drawing isUp (Move distance i)  =  Move distance $ drawing isUp i
drawing isUp (Turn angle i)  = Turn angle $ drawing isUp i
drawing isUp (SetColour color i)  = SetColour color $ drawing isUp i
drawing isUp (SetStyle linestyle i)  = SetStyle linestyle $ drawing isUp i
drawing isUp (PenDown i)  = drawing False i
drawing isUp (PenUp i)  = drawing True i

invisibly :: Instructions -> Instructions
invisibly i = PenUp (drawing False i)


-- Retracing Backwards (4 marks)
retrace :: Instructions -> Instructions
retrace i = revTrace i Stop start where
  revTrace :: Instructions -> Instructions -> TortoiseState -> Instructions
  revTrace (Move d i) acc (TortoiseState sp f s c p) = revTrace i (Move (-d) acc) (TortoiseState sp f s c p)
  revTrace (Turn a i) acc (TortoiseState sp f s c p) = revTrace i (Turn (-a) acc) (TortoiseState sp f s c p)
  -- line style, colour, and pen actions are off by one due to start state so we pass what they should be next
  revTrace (SetStyle nextStyle i) acc (TortoiseState sp f s c p) = revTrace i (SetStyle s acc) (TortoiseState sp f nextStyle c p)
  revTrace (SetColour nextColour i) acc (TortoiseState sp f s c p) = revTrace i (SetColour c acc) (TortoiseState sp f s nextColour p)
  revTrace (PenUp i) acc (TortoiseState sp f s c p)
      | p = revTrace i (PenDown acc)  (TortoiseState sp f s c False)
      | otherwise = revTrace i (PenUp acc) (TortoiseState sp f s c False)
  revTrace (PenDown i) acc (TortoiseState sp f s c p)
      | p = revTrace i (PenDown acc) (TortoiseState sp f s c True)
      | otherwise = revTrace i (PenUp acc) (TortoiseState sp f s c True)
  revTrace Stop acc _ = acc

--Overlaying images (4 marks)
overlay :: [Instructions] -> Instructions
overlay [] = Stop
overlay (x:xs) = x `andThen` ( retrace $ invisibly x) `andThen` (overlay xs)
                                        
