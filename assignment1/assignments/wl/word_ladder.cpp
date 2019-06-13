#include "assignments/wl/word_ladder.h"

bool bfsFindLadder(std::string src,
                   std::string des,
                   std::unordered_set<std::string> dict,
                   std::unordered_map<std::string, std::vector<std::string>>& kids);

void dfsGetPaths(const std::string& w,
                 const std::string& endw,
                 const std::unordered_map<std::string, std::vector<std::string>>& kids,
                 std::vector<std::string>& route,
                 std::vector<std::vector<std::string>>& path) {
  if (w == endw) {
    path.push_back(route);
    return;
  }

  const auto it = kids.find(w);
  if (it == kids.cend())
    return;

  for (const std::string& child : it->second) {
    route.push_back(child);
    dfsGetPaths(child, endw, kids, route, path);
    route.pop_back();
  }
}

bool bfsFindLadder(std::string src,
                   std::string des,
                   std::unordered_set<std::string> dict,
                   std::unordered_map<std::string, std::vector<std::string>>& kids) {
  unsigned int l = src.size();
  bool found = false;
  std::unordered_set<std::string> q1{src};
  std::unordered_set<std::string> q2{des};
  bool isForward = true;

  while (!q1.empty() && !q2.empty() && !found) {
    if (q1.size() > q2.size()) {
      swap(q1, q2);
      isForward = !isForward;
    }

    for (const std::string& w : q1)
      dict.erase(w);
    for (const std::string& w : q2)
      dict.erase(w);

    std::unordered_set<std::string> q;

    for (const std::string& word : q1) {
      for (unsigned int i = 0; i < l; i++) {
        std::string curr = word;

        for (int j = 'a'; j <= 'z'; j++) {
          curr[i] = j;

          const std::string* parent = &word;
          const std::string* kid = &curr;

          if (!isForward) {
            swap(parent, kid);
          }

          if (q2.count(curr)) {
            found = true;
            kids[*parent].push_back(*kid);
          } else if (dict.count(curr) && !found) {
            q.insert(curr);
            kids[*parent].push_back(*kid);
          }
        }
      }
    }
    swap(q, q1);
  }
  return found;
}

std::vector<std::vector<std::string>>
wordLadder(std::string src, std::string des, std::set<std::string> dic) {
  // des word is not in dic, no solution
  std::unordered_set<std::string> dict(dic.begin(), dic.end());
  if (!dict.count(des))
    return {};

  std::vector<std::vector<std::string>> result;

  unsigned int l = src.size();
  // filter words that has the same length as src word
  for (auto curr = dic.begin(); curr != dic.end(); ++curr) {
    if ((*curr).size() != l) {
      dict.erase(dict.find(*curr));
    }
  }

  std::unordered_map<std::string, std::vector<std::string>> kids;  // make descendants container

  // des word is in dic start find ladder
  if (bfsFindLadder(src, des, dict, kids)) {
    std::vector<std::string> path{src};
    dfsGetPaths(src, des, kids, path, result);
    sort(result.begin(), result.end());
  }

  return result;
}
