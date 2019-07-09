/*

  == Explanation and rational of testing ==

  Explain and justify how you approached testing, the degree
   to which you're certain you have covered all possibilities,
   and why you think your tests are that thorough.

*/

#include "assignments/ev/euclidean_vector.h"
#include "catch.h"

SCENARIO("Test Create EuclideanVector") {
  WHEN("Create EuclideanVector Class") {
    GIVEN("Only number of dimension")
    THEN("Get EuclideanVector with the given number of dimension") {
      EuclideanVector a{1};
      CHECK(a.GetNumDimensions() == 1);
      REQUIRE(a[0] == 0);
      int i{3};
      EuclideanVector b{i};
      CHECK(b.GetNumDimensions() == i);
      REQUIRE(b[0] == 0);
      REQUIRE(b[1] == 0);
      REQUIRE(b[2] == 0);
    }
    GIVEN("Number of dimension and magnitude")
    THEN("Get EuclideanVector with the given number of dimension, magnitude for each dimemsion") {
      EuclideanVector a{2, 4.0};

      CHECK(a.GetNumDimensions() == 2);
      REQUIRE(a[0] == 4.0);
      REQUIRE(a[1] == 4.0);

      int x{3};
      double y{3.24};
      EuclideanVector b{x, y};
      CHECK(b.GetNumDimensions() == x);
      REQUIRE(b[0] == y);
      REQUIRE(b[1] == y);
      REQUIRE(b[2] == y);
    }
    GIVEN("The start and end of an iterator to a std:vector")
    THEN(
        "Get EuclideanVector with magnitudes in each dimension according to the iterated values.") {
      std::vector<double> v{3, 4, 5};
      EuclideanVector a{v.begin(), v.end()};
      CHECK(a.GetNumDimensions() == 3);
      REQUIRE(a[0] == 3.0);
      REQUIRE(a[1] == 4.0);
      REQUIRE(a[2] == 5.0);
    }
    GIVEN("EuclideanVector a")
    THEN("Get EuclideanVector aCopy with Copy Constructor") {
      EuclideanVector a{2, 4.0};
      EuclideanVector aCopy{a};
      CHECK(a.GetNumDimensions() == aCopy.GetNumDimensions());
      REQUIRE(a == aCopy);
    }
    GIVEN("EuclideanVector a")
    THEN("Get EuclideanVector aMove with Move Constructor") {
      EuclideanVector a{2, 4.0};
      EuclideanVector aMove{std::move(a)};
      CHECK(a.GetNumDimensions() == 0);
      CHECK(aMove.GetNumDimensions() == 2);
      REQUIRE(aMove[0] == 4.0);
      REQUIRE(aMove[1] == 4.0);
    }
  }
}

SCENARIO("Test EuclideanVector Class operator overload") {
  WHEN("Copy EuclideanVector with Copy Assignment operator") {
    GIVEN("EuclideanVector a assigning the dimension and magnitudes to EuclideanVector b")
    THEN("Get EuclideanVector b with same dimension and magnitudes as a") {
      EuclideanVector a{2, 3.5};
      EuclideanVector b = a;
      CHECK(a.GetNumDimensions() == b.GetNumDimensions());
      REQUIRE(a == b);
    }
  }
  WHEN("Move EuclideanVector with Move Assignment operator") {
    GIVEN("EuclideanVector a moving the content to EuclideanVector b")
    THEN("Get EuclideanVector b take procession of a's dimension and magnitudes") {
      EuclideanVector a{2, 3.5};
      EuclideanVector b = std::move(a);
      CHECK(a.GetNumDimensions() == 0);
      CHECK(b.GetNumDimensions() == 2);
      REQUIRE(b[0] == 3.5);
      REQUIRE(b[1] == 3.5);
    }
  }
  WHEN("Subscript operator allows to get and set the value in a given dimension of the Euclidean "
       "Vector.") {
    EuclideanVector a{2, 3.5};
    GIVEN("A dimension of the Euclidean Vector.")
    THEN("Allows to get the value in a given dimension of EuclideanVector a") {
      CHECK(a[0] == 3.5);
    }
    GIVEN("A dimension of the Euclidean Vector.")
    THEN("Allows to set the value in a given dimension of EuclideanVector a") {
      double x{10.5};
      a[0] = x;
      REQUIRE(a[0] == x);
    }
  }
  GIVEN("Addition operater for adding vectors of the same dimension.") {
    EuclideanVector a{2, 3.5};
    EuclideanVector b{2, 4.5};
    EuclideanVector c{1};
    WHEN("EuclideanVector a and b have the same number of dimension")
    THEN("Each dimension of EuclideanVector a sum up the same magnitude of a and b") {
      a += b;
      REQUIRE(a[0] == 8);
      REQUIRE(a[1] == 8);
    }
    WHEN("EuclideanVector a and b have different number of dimension")
    THEN("Throw EuclideanVectorError Exception: Dimensions of LHS(X) and RHS(Y) do not match") {
      REQUIRE_THROWS_WITH(a += c, std::string{"Dimensions of LHS(X) and RHS(Y) do not match"});
    }
  }
  GIVEN("Subtraction operater for subtracting vectors of the same dimension.") {
    EuclideanVector a{2, 8};
    EuclideanVector b{2, 4.5};
    EuclideanVector c{1};

    WHEN("EuclideanVector a and b have the same number of dimension.")
    THEN("Each dimension of EuclideanVector a subtract the same magnitude of b") {
      a -= b;
      REQUIRE(a[0] == 3.5);
      REQUIRE(a[1] == 3.5);
    }
    WHEN("EuclideanVector a and b have different number of dimension")
    THEN("Throw EuclideanVectorError Exception: Dimensions of LHS(X) and RHS(Y) do not match") {
      REQUIRE_THROWS_WITH(a -= c, std::string{"Dimensions of LHS(X) and RHS(Y) do not match"});
    }
  }
  GIVEN("Multiplication operater for scalar multiplication.") {
    EuclideanVector a{2, 8};
    double b = .5;
    AND_GIVEN("A non-zero dimension of the Euclidean Vector.")
    THEN("Each dimension of EuclideanVector a multiplies scalar value b") {
      a *= b;
      REQUIRE(a[0] == 4);
      REQUIRE(a[1] == 4);
    }
  }
  GIVEN("Division operater for For scalar division.") {
    EuclideanVector a{2, 8};
    double b{0.5};
    AND_GIVEN("A non-zero dimension of the Euclidean Vector.")
    THEN("Each dimension of EuclideanVector a divides scalar value b") {
      a /= b;
      REQUIRE(a[0] == 16);
      REQUIRE(a[1] == 16);
    }
    WHEN("Scalar value b is zero.")
    THEN("Throw EuclideanVectorError Exception: Dimensions of LHS(X) and RHS(Y) do not match") {
      try {
        a /= 0;
      } catch (EuclideanVectorError& e) {
        REQUIRE(e.what() == std::string{"Invalid vector division by 0"});
      }
    }
  }
  GIVEN("Vector Type Conversion Operators for type casting to a std::vector") {
    std::vector<double> v(4, 5);
    EuclideanVector a(v.begin(), v.end());
    AND_GIVEN("EuclideanVector a created from a std::vector v")
    THEN("type casting a will return a equal to std::vector v") {
      CHECK(a.GetNumDimensions() == 4);
      REQUIRE(std::vector<double>{a} == v);
    }
  }
  GIVEN("List Type Conversion Operators for type casting to a std::list") {
    std::vector<double> v(4, 5);
    std::list<double> l(v.begin(), v.end());
    EuclideanVector a(v.begin(), v.end());
    AND_GIVEN("EuclideanVector a created from a std::list")
    THEN("type casting a will return a equal to std::list") {
      CHECK(a.GetNumDimensions() == 4);
      REQUIRE(std::list<double>{a} == l);
    }
  }
}
SCENARIO("Test EuclideanVector Class Methods") {
  EuclideanVector a{2, 3.5};

  WHEN("Calling EuclideanVector Class Methods double at(int index)")
  GIVEN("The index of the dimension of the EuclideanVector a") {
    THEN("Returns the value of the magnitude in the dimension given as the function parameter") {
      REQUIRE(a.at(0) == a[0]);
      REQUIRE(a.at(1) == a[1]);
    }
  }
  GIVEN("The index X is out of the range of the dimension of the EuclideanVector a") {
    THEN("Throw EuclideanVectorError Exception: Index X is not valid for this EuclideanVector "
         "object") {
      try {
        a.at(-1);
      } catch (EuclideanVectorError& e) {
        REQUIRE(e.what() == std::string{"Index -1 is not valid for this EuclideanVector object"});
      }
    }
  }

  WHEN("Calling EuclideanVector Class Methods double& at(int index)")
  GIVEN("The index of the dimension of the EuclideanVector a") {
    THEN(
        "Returns the reference of the magnitude in the dimension given as the function parameter") {
      REQUIRE(a.at(0) == a[0]);
      REQUIRE(a.at(1) == a[1]);
    }
  }
  GIVEN("The index X is out of the range of the dimension of the EuclideanVector a") {
    THEN("Throw EuclideanVectorError Exception: Index X is not valid for this EuclideanVector "
         "object") {
      try {
        a.at(-1);
      } catch (EuclideanVectorError& e) {
        REQUIRE(e.what() == std::string{"Index -1 is not valid for this EuclideanVector object"});
      }
    }
  }

  WHEN("Calling EuclideanVector Class Methods int GetNumDimensions()")
  THEN("Return the number of dimensions in a particular EuclideanVector") {
    REQUIRE(a.GetNumDimensions() == 2);
  }

  WHEN("Calling EuclideanVector Class Methods double GetEuclideanNorm()") {
    GIVEN("A non-zero dimension of the Euclidean Vector.") {
      THEN("Returns the Euclidean norm of the vector as a double.") {
        std::vector<double> v{1, 2, 3};
        EuclideanVector b{v.begin(), v.end()};
        REQUIRE(b.GetEuclideanNorm() == std::hypot(std::hypot(1, 2), 3));
      }
    }
    GIVEN("A zero dimension of the Euclidean Vector.") {
      THEN("Throw EuclideanVectorError Exception: EuclideanVector with no dimensions does not have "
           "a norm") {
        EuclideanVector c{0};
        try {
          c.GetEuclideanNorm();
        } catch (EuclideanVectorError& e) {
          REQUIRE(e.what() ==
                  std::string{"EuclideanVector with no dimensions does not have a norm"});
        }
      }
    }
    GIVEN("A non-zero dimension of the Euclidean Vector with zero Norm.") {
      THEN("Throw EuclideanVectorError Exception: EuclideanVector with euclidean normal of 0 does "
           "not have a unit vector") {
        EuclideanVector c{4};
        REQUIRE_THROWS_AS(c.GetEuclideanNorm(), EuclideanVectorError);
//        try {
//          c.GetEuclideanNorm();
//        } catch (EuclideanVectorError& e) {
//
////        REQUIRE_THROWS_WITH(c.GetEuclideanNorm(),
//            REQUIRE(e.what() ==
//            std::string{"EuclideanVector with euclidean normal of 0 does not have a unit vector"});
//        }
      }
    }
  }
}
SCENARIO("Test EuclideanVector Class friend functions") {
  WHEN("Compare two vectors if they are equal in the number of dimensions and the magnitude in "
       "each dimension is equal") {
    GIVEN("EuclideanVector a and b")
    THEN("Return True if they are equal in the number of dimensions and the magnitude in each "
         "dimension is equal") {
      EuclideanVector a{2, 3.5};
      EuclideanVector b = a;
      CHECK(a.GetNumDimensions() == b.GetNumDimensions());
      REQUIRE(a == b);
    }
    AND_THEN("Return False if they are not equal in the number of dimensions and the magnitude in each "
             "dimension is equal") {
      EuclideanVector a{2, 3.5};
      EuclideanVector b{2};
      REQUIRE_FALSE(a.GetNumDimensions() != b.GetNumDimensions());
      REQUIRE_FALSE(a == b);
    }
  }
  //  WHEN("Move EuclideanVector with Move Assignment operator") {
  //    GIVEN("EuclideanVector a moving the content to EuclideanVector b")
  //    THEN("Get EuclideanVector b take procession of a's dimension and magnitudes") {
  //      EuclideanVector a{2, 3.5};
  //      EuclideanVector b = std::move(a);
  //      CHECK(a.GetNumDimensions() == 0);
  //      CHECK(b.GetNumDimensions() == 2);
  //      REQUIRE(b[0] == 3.5);
  //      REQUIRE(b[1] == 3.5);
  //    }
  //  }
  //  WHEN("Subscript operator allows to get and set the value in a given dimension of the Euclidean
  //  "
  //       "Vector.") {
  //    EuclideanVector a{2, 3.5};
  //    GIVEN("A dimension of the Euclidean Vector.")
  //    THEN("Allows to get the value in a given dimension of EuclideanVector a") {
  //      CHECK(a[0] == 3.5);
  //    }
  //    GIVEN("A dimension of the Euclidean Vector.")
  //    THEN("Allows to set the value in a given dimension of EuclideanVector a") {
  //      double x{10.5};
  //      a[0] = x;
  //      REQUIRE(a[0] == x);
  //    }
  //  }
  GIVEN("Addition operater for adding vectors of the same dimension.") {
    EuclideanVector a{2, 3.5};
    EuclideanVector b{2, 4.5};
    EuclideanVector c{1};
    WHEN("EuclideanVector a and b have the same number of dimension")
    THEN("Each dimension of EuclideanVector a sum up the same magnitude of a and b") {
      a += b;
      REQUIRE(a[0] == 8);
      REQUIRE(a[1] == 8);
    }
    WHEN("EuclideanVector a and b have different number of dimension")
    THEN("Throw EuclideanVectorError Exception: Dimensions of LHS(X) and RHS(Y) do not match") {
      try {
        a += c;
      } catch (EuclideanVectorError& e) {
        REQUIRE(e.what() == std::string{"Dimensions of LHS(X) and RHS(Y) do not match"});
      }
    }
  }
  GIVEN("Subtraction operater for subtracting vectors of the same dimension.") {
    EuclideanVector a{2, 8};
    EuclideanVector b{2, 4.5};
    EuclideanVector c{1};

    WHEN("EuclideanVector a and b have the same number of dimension.")
    THEN("Each dimension of EuclideanVector a subtract the same magnitude of b") {
      a -= b;
      REQUIRE(a[0] == 3.5);
      REQUIRE(a[1] == 3.5);
    }
    WHEN("EuclideanVector a and b have different number of dimension")
    THEN("Throw EuclideanVectorError Exception: Dimensions of LHS(X) and RHS(Y) do not match") {
      REQUIRE_THROWS_WITH(a -= c, std::string{"Dimensions of LHS(X) and RHS(Y) do not match"});
    }
  }
  GIVEN("Multiplication operater for scalar multiplication.") {
    EuclideanVector a{2, 8};
    double b = .5;
    AND_GIVEN("A non-zero dimension of the Euclidean Vector.")
    THEN("Each dimension of EuclideanVector a multiplies scalar value b") {
      a *= b;
      REQUIRE(a[0] == 4);
      REQUIRE(a[1] == 4);
    }
  }
  GIVEN("Division operater for For scalar division.") {
    EuclideanVector a{2, 8};
    double b{0.5};
    AND_GIVEN("A non-zero dimension of the Euclidean Vector.")
    THEN("Each dimension of EuclideanVector a divides scalar value b") {
      a /= b;
      REQUIRE(a[0] == 16);
      REQUIRE(a[1] == 16);
    }
    WHEN("Scalar value b is zero.")
    THEN("Throw EuclideanVectorError Exception: Dimensions of LHS(X) and RHS(Y) do not match") {
      REQUIRE_THROWS_WITH(a /= 0, std::string{"Invalid vector division by 0"});
    }
  }
}
