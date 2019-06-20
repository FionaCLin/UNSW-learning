#include <iostream>
#include "assignments/wl/word_ladder.h"


void DfsCollectPaths(const std::string& word,
                     const std::string& end_word,
                     const std::unordered_map<std::string, std::vector<std::string>>& kids,
                     std::vector<std::string>& route,
                     std::vector<std::vector<std::string>>& path) {
  if (word == end_word) {
    path.push_back(route);
    return;
  }

  const auto it = kids.find(word);
  if (it == kids.cend())
    return;

  for (const std::string& child : it->second) {
    route.push_back(child);
    DfsCollectPaths(child, end_word, kids, route, path);
    route.pop_back();
  }
}

std::vector<std::string> GetAdjWord(std::string word) {
  std::cout << word << ":\n";
  std::vector<std::string> res;
  for (unsigned int i = 0; i < word.size(); i++) {
    std::string curr = word;
    char c = word[i];
    for (int j = 'a'; j <= 'z'; j++) {
      if (j == c) continue;
      curr[i] = j;
      res.emplace_back(curr);
      std::cout <<"\t, \""<< curr << '"';
    }
  }
  std::cout << "\n";
  return res;
}


bool BfsCheckLadder(std::string src,
                   std::string des,
                   std::unordered_set<std::string>& dict,
                   std::unordered_map<std::string, std::vector<std::string>>& kids) {
  bool found = false;
  std::unordered_set<std::string> forward_queue{src};
  std::unordered_set<std::string> backward_queue{des};
  bool is_forward = true;

  while (!forward_queue.empty() && !backward_queue.empty() && !found) {
    if (forward_queue.size() > backward_queue.size()) {
      swap(forward_queue, backward_queue);
      is_forward = !is_forward;
    }

    for (const std::string& w : forward_queue)
      dict.erase(w);
    for (const std::string& w : backward_queue)
      dict.erase(w);

    std::unordered_set<std::string> q;

    for (const std::string& word : forward_queue) {
      auto adj_words = GetAdjWord(word);
      for (const auto& curr : adj_words) {
        const std::string* parent = &word;
        const std::string* kid = &curr;

        if (!is_forward) {
          swap(parent, kid);
        }

        if (!backward_queue.count(curr) && !(dict.count(curr) && !found)) continue;
        else if (backward_queue.count(curr)) {
          found = true;
        } else if (dict.count(curr) && !found) {
          q.insert(curr);
        }
        kids[*parent].push_back(*kid);
      }
    }

    swap(q, forward_queue);
  }
  return found;
}

std::vector<std::vector<std::string>>
WordLadder(std::string src, std::string des, std::unordered_set<std::string> dic) {
  // des word is not in dic, no solution

  if (!dic.count(des))
    return {};

  std::vector<std::vector<std::string>> result;

  unsigned int l = src.size();
  // filter words that has the same length as src word
  for (auto curr = dic.begin(); curr != dic.end(); ++curr) {
    if ((*curr).size() != l) {
      dic.erase(dic.find(*curr));
    }
  }

  std::unordered_map<std::string, std::vector<std::string>> kids;  // make descendants container

  // des word is in dic start find ladder
  if (BfsCheckLadder(src, des, dic, kids)) {
    std::vector<std::string> path{src};
    DfsCollectPaths(src, des, kids, path, result);
//    for (const auto& r : result) {
//      for(const auto& w : r) {
//        if (w == r.back() && w == r.front()) {
//          std::cout <<"\""<< w << "\"\n";
//        } else if (w == r.back()) {
//          std::cout <<"\""<< w << "\"\n";
//        } else  {
//          std::cout <<"\""<< w << "\",\t";
//        }
//      }
//    }
    sort(result.begin(), result.end());
  }

  return result;
}
