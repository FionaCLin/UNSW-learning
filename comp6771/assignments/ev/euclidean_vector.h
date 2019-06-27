// TODO(you): Include header guards
#ifndef EUCLIDEAN_VECTOR_H
#define EUCLIDEAN_VECTOR_H

#include <exception>
#include <string>
#include <memory>
#include <vector>

class EuclideanVectorError : public std::exception {
 public:
  explicit EuclideanVectorError(const std::string& what) : what_(what) {}
  const char* what() const noexcept{ return what_.c_str(); }
 private:
  std::string what_;
};

class EuclideanVector {
 public:

  explicit EuclideanVector(int i);
  EuclideanVector(int, double);
  EuclideanVector(std::vector<double>::const_iterator, std::vector<double >::const_iterator);
//  ~EuclideanVector();



  double at(int);
  double GetEuclideanNorm();
  EuclideanVector CreateUnitVector();


  friend std::ostream& operator<<(std::ostream& os, const EuclideanVector& v);

  int GetNumDimensions();
 private:
  std::unique_ptr<double[]> magnitudes_;
  int size_;
};
#endif