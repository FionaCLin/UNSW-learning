
module Art where  

import ShapeGraphics
import Codec.Picture

art :: Picture
art = fTree (Point (400-width/2) 800) (Vector 0 (-height)) (Vector width 0) (toBlue (Colour 0 255 0 125)) depth
  where
    depth = 8
    height = 100
    width = 12
    angle = pi/6

    toBlue :: Colour -> Colour
    toBlue (Colour r g b o) = 
      Colour r (min 255 (g - 25)) (max 0 (b + 25)) (max 0 (o - 12))
    toOrange (Colour r g b o) = 
      Colour (max 0 (r + 25)) (max 0 (g - 25)) b (max 0 (o - 12))
   
    fTree :: Point -> Vector -> Vector -> Colour -> Int -> Picture
    fTree pos vec1 vec2 col n
      | n == 0     = []
      | otherwise  = [ Polygon [ pos, 
                                  movePoint vec1 pos, 
                                  movePoint vec2 (movePoint vec1 pos) ,
                                  movePoint vec2 pos ] 
                                col Solid SolidFill] ++ 
                      fTree (movePoint vec1 pos) 
                          (scaleVector 0.95 $ rotateVector (-0.85* angle) vec1)
                          (scaleVector 0.99 $ rotateVector (-0.95* angle) vec2) 
                          (toBlue col) (n - 1) ++
                      fTree (movePoint vec1 pos) 
                            (scaleVector 0.28 $ rotateVector (-0.95 *angle) vec1)
                            (scaleVector 0.85 $ rotateVector (-0.95 *angle) vec2) 
                            (toOrange col) (n - 1) ++
                      fTree (movePoint vec1 pos) 
                            (scaleVector 0.85 $ rotateVector (-0.5 *angle) vec1)
                            (scaleVector 0.28 $ rotateVector (-0.75 *angle) vec2) 
                            (toOrange col) (n - 1) ++
                      fTree (movePoint vec1 pos) 
                            (scaleVector 0.28 $ rotateVector (0.95 *angle) vec1)
                            (scaleVector 0.85 $ rotateVector (0.95 *angle) vec2) 
                            (toOrange col) (n - 1) ++
                      fTree (movePoint vec1 pos) 
                            (scaleVector 0.85 $ rotateVector (0.75 *angle) vec1)
                            (scaleVector 0.28 $ rotateVector (0.5 *angle) vec2) 
                            (toOrange col) (n - 1) ++
                      fTree (movePoint vec1 pos) 
                            (scaleVector 0.95 $ rotateVector (0.85 * angle) vec1)
                            (scaleVector 0.99 $ rotateVector (0.95 * angle) vec2) 
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

  