/*

  == Explanation and rational of testing ==

  Explain and justify how you approached testing, the degree
   to which you're certain you have covered all possibilities,
   and why you think your tests are that thorough.

*/

#include "assignments/ev/euclidean_vector.h"
#include "catch.h"

//// Function definitions can have auto return types in C++17
// auto EqualsString([[maybe_unused]] const std::vector<std::string>& strings) {
//  return Catch::Predicate<RopeView>([] ([[maybe_unused]] const RopeView expected) {
//    return true;
//  });
//}

SCENARIO() {
  WHEN("Test create vector") {

    GIVEN("Only number of dimension")
    //    std::string emptyString;
    //    const RopeView& result = Split(emptyString, ' ');
    THEN("Create EuclideanVector with the given number of dimension") {
      int numOfDim = 1;
      EuclideanVector v1(numOfDim);
      CHECK(v1.GetNumDimensions() == numOfDim);
      REQUIRE(v1[0] == 0);
      //      REQUIRE(std::distance(result[0].first, result[0].second) == 0);
      //      // Alternatively, we can use predicates.

      //      REQUIRE_THAT(result, EqualsString({""}));
      //    }

    }
    GIVEN("Number of dimension and magnitude")
    //    std::string emptyString;
    //    const RopeView& result = Split(emptyString, ' ');
    THEN("Create EuclideanVector with the given number of dimension, magnitude for each dimemsion") {
      int numOfDim = 1;
      double magnitude = 2.5;
      EuclideanVector v2{numOfDim, magnitude};
      CHECK(v2.GetNumDimensions() == numOfDim);
      REQUIRE(v2[0] == magnitude);
    }
      //      CHECK(result.size() == 1);
      //      REQUIRE(std::distance(result[0].first, result[0].second) == 0);
      //      // Alternatively, we can use predicates.

      //      REQUIRE_THAT(result, EqualsString({""}));
      //    }

    }
  }

  // SCENARIO("Do that thing with the thing", "[Tags]") {
  //  GIVEN("This stuff exists") {
  //    // make stuff exist
  //    AND_GIVEN("And some assumption") {
  //      // Validate assumption
  //      WHEN("I do this") {
  //        // do this
  //        THEN("it should do this") {
  //          // REQUIRE(itDoesThis());
  //          AND_THEN("do that") {
  //            // REQUIRE(itDoesThat());
  //          }
  //        }
  //      }
  //    }
  //  }
  //}