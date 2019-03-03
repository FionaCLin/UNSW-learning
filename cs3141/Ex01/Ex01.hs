module Ex01 where

-- needed to display the picture in the playground
import Codec.Picture

-- our line graphics programming interface
import ShapeGraphics

-- Part 1
-- picture of a house
housePic :: Picture
housePic = [door, house]
  where
    house :: PictureObject
    house = Path (mkPoints houseCOs) green Solid 
    door :: PictureObject
    door  = Path (mkPoints doorCOs) red Solid 
-- these are the coordinates - convert them to a list of Point
mkPoints :: [(Float, Float)] -> [Point]
mkPoints = map(\(a, b) -> Point a b) 

houseCOs :: [(Float, Float)]
houseCOs = [(300, 750), (300, 450), (270, 450), (500, 200),
         (730, 450), (700, 450), (700, 750)]

doorCOs :: [(Float, Float)]
doorCOs = [(420, 750), (420, 550), (580, 550), (580, 750)]

grey :: Colour
grey = Colour 255 255 255 128

smoke :: PictureObject
smoke = Path (mkPoints smokeCOs) grey Solid
smokeCOs :: [(Float, Float)]
smokeCOs = [(635, 240), (625, 230), (635, 220), (625, 210)]

chimneyHouse :: Picture
chimneyHouse = [door, chimneyHouse, smoke]
  where
    door :: PictureObject
    door  = Path (mkPoints doorCOs) red Solid 
    chimneyHouse :: PictureObject
    chimneyHouse = Path (mkPoints chimneyHouseCOs) green Solid
chimneyHouseCOs :: [(Float, Float)]
chimneyHouseCOs = [(300, 750), (300, 450), (270, 450), (500, 200), (615, 325), (615, 250), (650, 250), (650, 363),(730, 450), (700, 450), (700, 750)]


-- Part 2
movePoint :: Point -> Vector -> Point
movePoint (Point x y) (Vector xv yv)
  = Point (x + xv) (y + yv)

-- myMap :: (a  ->  b) -> [a] -> [b]
mvPoints fn [] vec = [] 
mvPoints fn (x:xs) (Vector xv yv)
  = fn x (Vector xv yv) : mvPoints fn xs (Vector xv yv) 


movePictureObject :: Vector -> PictureObject -> PictureObject
movePictureObject vec (Path points colour lineStyle) = Path (mvPoints movePoint (points) vec) colour lineStyle

movePictureObject vec (Circle centre radius colour lineStyle fillStyle) = Circle (movePoint centre vec) radius colour lineStyle fillStyle

-- add other cases
movePictureObject vec (Polygon points colour lineStyle fillStyle) = Polygon (mvPoints movePoint (points) vec) colour lineStyle fillStyle

movePictureObject vec (Ellipse centre h w r colour lineStyle fillStyle) = Ellipse  (movePoint centre vec) h w r colour lineStyle fillStyle

-- Part 3


-- generate the picture consisting of circles:
-- [Circle (Point 400 400) (400/n) col Solid SolidFill,
--  Circle (Point 400 400) 2 * (400/n) col Solid SolidFill,
--  ....
--  Circle (Point 400 400) 400 col Solid SolidFill]

simpleCirclePic :: Colour -> Float -> Picture
simpleCirclePic col n = map(\r -> Circle (Point 400 400) r col Solid SolidFill) (enumFromThenTo (400/n) (2*(400/n)) 400) 


-- use 'writeToFile' to write a picture to file "ex01.png" to test your
-- program if you are not using Haskell for Mac
-- e.g., call
-- writeToFile [house, door]

writeToFile pic
  = writePng "ex01.png" (drawPicture 3 pic)
