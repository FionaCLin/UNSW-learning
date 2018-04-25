module Art where  

import ShapeGraphics
import Codec.Picture

art :: Picture
art = fracTree 15 100 6 10 -- replace with something else

fracTree :: Float -> Float -> Float -> Int -> Picture
fracTree width height r n
  = fTree (Point  (400 - width / 2) 800) r (Vector 0 (-height))
                  (Vector width 0) red n
  where
    
    toBlue :: Colour -> Colour
    toBlue (Colour r g b o) = 
      Colour (max 0 (r - 15)) g (min 255 (b + 15)) o
    angle = pi/8
    fTree :: Point -> Float -> Vector -> Vector -> Colour -> Int -> Picture
    fTree pos r vec1 vec2 col n
      | n <= 1 = [Circle  (movePoint vec1 pos)
                          r
                          col
                          Solid
                          SolidFill]
                          
      | otherwise = fTree pos r vec1 vec2 col (n - 1) ++
                    fTree (movePoint vec1 pos) r
                          (scaleVector 0.8 $ rotateVector (-0.5 * angle) vec1)
                          (scaleVector 0.8 $ rotateVector (-0.5 * angle) vec2) 
                          (toBlue col) (n - 1) ++
                    fTree (movePoint vec1 pos) r
                          (scaleVector 0.8 $ rotateVector (-angle) vec1)
                          (scaleVector 0.8 $ rotateVector (-angle) vec2) 
                          (toBlue col) (n - 1) 

      
scaleVector :: Float -> Vector -> Vector
scaleVector fac (Vector x y)
  = Vector (fac * x) (fac * y)                           
 
rotateVector :: Float -> Vector -> Vector
rotateVector alpha (Vector vx vy)
  = Vector (cos alpha * vx - sin alpha * vy)
           (sin alpha * vx + cos alpha * vy)

movePoint :: Vector -> Point -> Point
movePoint (Vector xv yv) (Point xp yp)
  = Point (xv + xp) (yv + yp)

-- use 'writeToFile' to write a picture to file "ex01.png" to test your
-- program if you are not using Haskell for Mac
-- e.g., call
-- writeToFile [house, door]

writeToFile pic
  = writePng "art.png" (drawPicture 3 art)
