#include "assignments/wl/lexicon.h"
#include "assignments/wl/word_ladder.h"

#include <iostream>
#include <string>

int main() {
  auto lexicon = GetLexicon("assignments/wl/words.txt");

  std::string src = "";
  std::string des = "";
  std::cout << "Enter start word (RETURN to quit): ";
  std::cin >> src;
  std::cout << "Enter destination word: ";
  std::cin >> des;

  auto result = WordLadder(src, des, lexicon);

  if (result.size() == 0) {
    std::cout << "No ladder found.\n";
  } else {
    std::cout << "Found ladder: ";
    for (const auto& wordList : result) {
      for (const auto& word : wordList) {
        if (word == wordList.back()) {
          std::cout << word;
        } else {
          std::cout << word << ' ';
        }
      }
      std::cout << '\n';
    }
  }

  return 0;
}
