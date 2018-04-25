
drawMandelbrot :: Coordinate -> Color
drawMandelbrot (x,y) = colorIterations $ mandelbrot (x :+ y) (0 :+ 0) 0

mandelbrot :: Complex Double -- Coordinate to test
           -> Complex Double -- Iterating Z value
           -> Int -- Current iteration
           -> Int -- Iterations before diverging
mandelbrot c z iter
    | iter > maxIter = 0
    | otherwise = let z' = z^2 + c in
                  if magnitude(z') > 2
                  then iter
                  else mandelbrot c z' (iter+1)
