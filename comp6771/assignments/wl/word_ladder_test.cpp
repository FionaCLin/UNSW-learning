/*
Approach and rational:
* Getting the expected output from reference solution with various sample inputs as my test cases, the below tests are developed for each functions in world_ladder.cpp.

* It covers the most general test cases include the expected path and unexpected path such as get adjecent word of empty string and DFS find paths with empty routes etc.

* Providing the sample input, get the actual output(aka res variable in test) from the tested function.

* Then compare and assert the expected output with the output.

* By doing this way, we can ensure the function behave the way as described in test. No matter how the actual implementation change, we can ensure the function correctness.

*/
#include "assignments/wl/lexicon.h"
#include "assignments/wl/word_ladder.h"
#include "catch.h"

auto lexicon = GetLexicon("assignments/wl/words.txt");
std::vector<std::vector<std::string>> expect;

// Tests for WordLadder function

TEST_CASE("WordLadder not found for airplane to tricycle") {
  REQUIRE((WordLadder("airplane", "tricycle", lexicon)).size() == 0);
}

TEST_CASE("WordLadder found for con to cat") {
  auto res = WordLadder("con", "cat", lexicon);

  expect = {{"con", "can", "cat"}, {"con", "cot", "cat"}};

  REQUIRE(res.size() == expect.size());

  for (unsigned int i = 0; i < res.size(); i++) {
    for (unsigned int j = 0; j < res[i].size(); j++) {
      REQUIRE(res[i][j] == expect[i][j]);
    }
  }
}

TEST_CASE("WordLadder found for awake to sleep") {
  auto res = WordLadder("awake", "sleep", lexicon);

  expect = {
      {"awake", "aware", "sware", "share", "sharn", "shawn", "shewn", "sheen", "sheep", "sleep"},
      {"awake", "aware", "sware", "share", "shire", "shirr", "shier", "sheer", "sheep", "sleep"}};

  REQUIRE(res.size() == expect.size());

  for (unsigned int i = 0; i < res.size(); i++) {
    for (unsigned int j = 0; j < res[i].size(); j++) {
      REQUIRE(res[i][j] == expect[i][j]);
    }
  }
}

TEST_CASE("WordLadder found for work to play") {
  auto res = WordLadder("work", "play", lexicon);

  expect = {{"work", "fork", "form", "foam", "flam", "flay", "play"},
            {"work", "pork", "perk", "peak", "pean", "plan", "play"},
            {"work", "pork", "perk", "peak", "peat", "plat", "play"},
            {"work", "pork", "perk", "pert", "peat", "plat", "play"},
            {"work", "pork", "porn", "pirn", "pian", "plan", "play"},
            {"work", "pork", "port", "pert", "peat", "plat", "play"},
            {"work", "word", "wood", "pood", "plod", "ploy", "play"},
            {"work", "worm", "form", "foam", "flam", "flay", "play"},
            {"work", "worn", "porn", "pirn", "pian", "plan", "play"},
            {"work", "wort", "bort", "boat", "blat", "plat", "play"},
            {"work", "wort", "port", "pert", "peat", "plat", "play"},
            {"work", "wort", "wert", "pert", "peat", "plat", "play"}};

  REQUIRE(res.size() == expect.size());

  for (unsigned int i = 0; i < res.size(); i++) {
    for (unsigned int j = 0; j < res[i].size(); j++) {
      REQUIRE(res[i][j] == expect[i][j]);
    }
  }
}

TEST_CASE("WordLadder found for knows to think") {
  auto res = WordLadder("knows", "think", lexicon);

  expect = {{"knows", "knots", "knits", "snits", "shits", "shins", "thins", "think"},
            {"knows", "knots", "snots", "shots", "shits", "shins", "thins", "think"},
            {"knows", "knots", "snots", "snits", "shits", "shins", "thins", "think"},
            {"knows", "snows", "shows", "shews", "thews", "thens", "thins", "think"},
            {"knows", "snows", "shows", "shoes", "shies", "shins", "thins", "think"},
            {"knows", "snows", "shows", "shops", "ships", "shins", "thins", "think"},
            {"knows", "snows", "shows", "shots", "shits", "shins", "thins", "think"},
            {"knows", "snows", "snots", "shots", "shits", "shins", "thins", "think"},
            {"knows", "snows", "snots", "snits", "shits", "shins", "thins", "think"}};

  REQUIRE(res.size() == expect.size());

  for (unsigned int i = 0; i < res.size(); i++) {
    for (unsigned int j = 0; j < res[i].size(); j++) {
      REQUIRE(res[i][j] == expect[i][j]);
    }
  }
}

TEST_CASE("840 result found from WordLadder for atlases to cabaret") {
  auto res = WordLadder("atlases", "cabaret", lexicon);

  REQUIRE(res.size() == 840);
}

// Tests for BfsCheckLadder function
TEST_CASE("Return true from BfsCheckLadder cog to hot") {
  std::unordered_map<std::string, std::vector<std::string>> expect_res(
      {{"cot", {"hot"}},
       {"cog", {"bog", "dog", "fog", "hog", "jog", "log", "mog", "nog", "tog", "wog", "cob", "cod",
                "col", "con", "coo", "cop", "cor", "cos", "cot", "cow", "cox", "coy", "coz"}},
       {"hog", {"hot"}},
       {"bot", {"hot"}}});

  std::unordered_map<std::string, std::vector<std::string>> res;
  REQUIRE(BfsCheckLadder("cog", "hot", lexicon, res) == true);
  for (auto& list : res) {
    std::unordered_map<std::string, std::vector<std::string>>::const_iterator found =
        expect_res.find(list.first);
    REQUIRE(found != expect_res.end());
    std::vector<std::string> expect_list = found->second;
    for (auto const& word : list.second) {
      REQUIRE(std::find(expect_list.begin(), expect_list.end(), word) != expect_list.end());
    }
  }
}

TEST_CASE("Return false from BfsCheckLadder a to bb") {
  std::unordered_map<std::string, std::vector<std::string>> res;
  REQUIRE(BfsCheckLadder("a", "bb", lexicon, res) == false);
}

// Tests for DfsCollectPaths function
TEST_CASE("Return Paths from DfsCollectPaths cog to hot") {
  std::unordered_map<std::string, std::vector<std::string>> kids(
      {{"cot", {"hot"}},
       {"cog", {"bog", "dog", "fog", "hog", "jog", "log", "mog", "nog", "tog", "wog", "cob", "cod",
                "col", "con", "coo", "cop", "cor", "cos", "cot", "cow", "cox", "coy", "coz"}},
       {"hog", {"hot"}},
       {"bot", {"hot"}}});

  expect = {{"cog", "hog", "hot"}, {"cog", "cot", "hot"}};
  std::vector<std::string> path{"cog"};
  std::vector<std::vector<std::string>> res;
  DfsCollectPaths("cog", "hot", kids, path, res);

  for (unsigned int i = 0; i < res.size(); i++) {
    for (unsigned int j = 0; j < res[i].size(); j++) {
      REQUIRE(res[i][j] == expect[i][j]);
    }
  }
}

TEST_CASE("Return empty Paths from DfsCollectPaths") {
  std::unordered_map<std::string, std::vector<std::string>> kids({});
  expect = {};
  std::vector<std::string> path{"cog"};
  std::vector<std::vector<std::string>> res;
  DfsCollectPaths("cog", "hot", kids, path, res);
  REQUIRE(res.size() == 0);
}

// tests for GetAdjWord
TEST_CASE("Found all adjacent words of hot") {
  std::vector<std::string> expect_neighbor = {
      "aot", "bot", "cot", "dot", "eot", "fot", "got", "iot", "jot", "kot", "lot", "mot", "not",
      "oot", "pot", "qot", "rot", "sot", "tot", "uot", "vot", "wot", "xot", "yot", "zot", "hat",
      "hbt", "hct", "hdt", "het", "hft", "hgt", "hht", "hit", "hjt", "hkt", "hlt", "hmt", "hnt",
      "hpt", "hqt", "hrt", "hst", "htt", "hut", "hvt", "hwt", "hxt", "hyt", "hzt", "hoa", "hob",
      "hoc", "hod", "hoe", "hof", "hog", "hoh", "hoi", "hoj", "hok", "hol", "hom", "hon", "hoo",
      "hop", "hoq", "hor", "hos", "hou", "hov", "how", "hox", "hoy", "hoz"};
  std::vector<std::string> res = GetAdjWord("hot");
  REQUIRE(res.size() == expect_neighbor.size());
  for (unsigned int i = 0; i < res.size(); i++) {
    REQUIRE(res[i] == expect_neighbor[i]);
  }
}

TEST_CASE("Found no adjacent word of empty string") {
  std::vector<std::string> res = GetAdjWord("");
  REQUIRE(res.size() == 0);
}
