module Art where  

import Data.Complex

-- needed to display the picture in the playground
import Codec.Picture

-- our line graphics programming interface
import ShapeGraphics

-- Part 1
-- picture of a house
housePic :: Float -> Float -> Float -> (Float, Picture)
housePic x y r = (r, [house])
  where
    house :: PictureObject
    house = Path (mkPoints (mkline(x, y)) ) green Solid 
   
-- these are the coordinates - convert them to a list of Point
mkPoints :: [(Float, Float)] -> [Point]
mkPoints = map(\(a, b) -> Point a b) 

mkline :: (Float, Float) -> [(Float, Float)]
mkline = \((a, b)) -> (a,b) : [(0.8 * a, 0.8 * b)] 

-- lineCOs :: [(Float, Float)]
-- lineCOs = [(300, 750), (300, 450)]


writeToFile r pic
  = writePng "tree-art.png" (drawPicture r pic)
