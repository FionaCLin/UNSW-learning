// TODO(you): Include header guards
#ifndef COMP6771_ASSIGNMENTS_EV_EUCLIDEAN_VECTOR_H_
#define COMP6771_ASSIGNMENTS_EV_EUCLIDEAN_VECTOR_H_

#include <algorithm>  // Look at these - they are helpful https://en.cppreference.com/w/cpp/algorithm
#include <cmath>
#include <exception>
#include <iostream>
#include <iterator>
#include <list>
#include <memory>
#include <string>
#include <vector>

class EuclideanVectorError : public std::exception {
 public:
  explicit EuclideanVectorError(const std::string& what) : what_(what) {}
  const char* what() const noexcept { return what_.c_str(); }

 private:
  std::string what_;
};

class EuclideanVector {
 public:
  explicit EuclideanVector(int i);
  EuclideanVector(int, double);
  EuclideanVector(std::vector<double>::const_iterator, std::vector<double>::const_iterator);
  EuclideanVector(const EuclideanVector&);
  EuclideanVector(EuclideanVector&&) noexcept;

  ~EuclideanVector() noexcept;

  EuclideanVector& operator=(const EuclideanVector&);
  EuclideanVector& operator=(EuclideanVector&&);
  double& operator[](int i);  // setting via []
  double operator[](int i) const; // getting via []
  EuclideanVector& operator+=(const EuclideanVector& ev);
  EuclideanVector& operator-=(const EuclideanVector& ev);
  EuclideanVector& operator*=(const double& val);
  EuclideanVector& operator/=(const double& val);
  operator std::vector<double>();
  operator std::list<double>();

  double at(int) const;
  double& at(int);
  int GetNumDimensions();
  double GetEuclideanNorm();
  EuclideanVector CreateUnitVector();

  friend bool operator==(const EuclideanVector& v1, const EuclideanVector& v2);
  friend bool operator!=(const EuclideanVector& v1, const EuclideanVector& v2);
  friend EuclideanVector operator+(const EuclideanVector& v1, const EuclideanVector& v2);
  friend EuclideanVector operator-(const EuclideanVector& v1, const EuclideanVector& v2);
  friend EuclideanVector operator*(const EuclideanVector& v1, const double& num);  // scalar product
  friend EuclideanVector operator/(const EuclideanVector& v1, const double& num);  // scalar product
  friend double operator*(const EuclideanVector& v1, const EuclideanVector& v2);   // dot product
  friend std::ostream& operator<<(std::ostream& os, const EuclideanVector& v);

 private:
  std::unique_ptr<double[]> magnitudes_;
  int numOfDimension_;
};
#endif  // COMP6771_ASSIGNMENTS_EV_EUCLIDEAN_VECTOR_H_
