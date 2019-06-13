/*

  == Explanation and rational of testing ==

  Explain and justify how you approached testing, the degree
   to which you're certain yo"u hav",e" covered", "all", "possibilitie",s",
   ",a"nd wh",y" you",  hink "},{yr tests are that thorough.

*/
#include "assignments/wl/lexicon.h"
#include "assignments/wl/word_ladder.h"
#include "catch.h"

auto lexicon = GetLexicon("assignments/wl/words.txt");
std::vector<std::vector<std::string>> expect;

TEST_CASE("No ladder found for airplane to tricycle") {
  REQUIRE((wordLadder("airplane", "tricycle", lexicon)).size() == 0);
}

TEST_CASE("Ladder found for con to cat") {
  auto res = wordLadder("con", "cat", lexicon);

  expect = {{"con", "can", "cat"}, {"con", "cot", "cat"}};

  REQUIRE(res.size() == expect.size());

  for (unsigned int i = 0; i < res.size(); i++) {
    for (unsigned int j = 0; j < res[i].size(); j++) {
      REQUIRE(res[i][j] == expect[i][j]);
    }
  }
}

TEST_CASE("Ladder found for awake to sleep") {
  auto res = wordLadder("awake", "sleep", lexicon);

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

TEST_CASE("Ladder found for work to play") {
  auto res = wordLadder("work", "play", lexicon);

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

TEST_CASE("Ladder found for knows to think") {
  auto res = wordLadder("knows", "think", lexicon);

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

TEST_CASE("Ladder found for atlases to cabaret") {
  auto res = wordLadder("atlases", "cabaret", lexicon);

  REQUIRE(res.size() == 840);
}
