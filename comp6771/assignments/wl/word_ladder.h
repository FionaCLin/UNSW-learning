#ifndef ASSIGNMENTS_WL_WORD_LADDER_H_
#define ASSIGNMENTS_WL_WORD_LADDER_H_

#include <set>
#include <unordered_set>
#include <unordered_map>
#include <vector>
#include <string>
#include <algorithm>
#include <utility>


bool BfsCheckLadder(std::string src,
                   std::string des,
                   std::unordered_set<std::string>& dict,
                   std::unordered_map<std::string, std::vector<std::string>>& kids) ;

void DfsCollectPaths(const std::string& word,
                     const std::string& end_word,
                     const std::unordered_map<std::string, std::vector<std::string>>& kids,
                     std::vector<std::string>& route,
                     std::vector<std::vector<std::string>>& path) ;

std::vector<std::string> GetAdjWord(std::string word) ;

std::vector<std::vector<std::string>> WordLadder(std::string src, std::string des, std::unordered_set<std::string> dic);

#endif  // ASSIGNMENTS_WL_WORD_LADDER_H_

