#ifndef COMP6771_ASSIGNMENTS_EV_EUCLIDEAN_VECTOR_H_
#define COMP6771_ASSIGNMENTS_EV_EUCLIDEAN_VECTOR_H_

#include <algorithm>  // Look at these - they are helpful https://en.cppreference.com/w/cpp/algorithm
#include <cassert>
#include <cmath>
#include <exception>
#include <iostream>
#include <iterator>
#include <list>
#include <memory>
#include <string>
#include <utility>
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
  explicit EuclideanVector(int numOfDimension = 1);
  EuclideanVector(int numOfDimension, double magnitudes);
  EuclideanVector(std::vector<double>::const_iterator start,
                  std::vector<double>::const_iterator end);
  EuclideanVector(const EuclideanVector& vector);
  EuclideanVector(EuclideanVector&& vector) noexcept;

  ~EuclideanVector() noexcept;

  EuclideanVector& operator=(const EuclideanVector& vector);
  EuclideanVector& operator=(EuclideanVector&& vector);
  double& operator[](int index);       // setting via []
  double operator[](int index) const;  // getting via []
  EuclideanVector& operator+=(const EuclideanVector& vector);
  EuclideanVector& operator-=(const EuclideanVector& vector);
  EuclideanVector& operator*=(double num);
  EuclideanVector& operator/=(double num);
  explicit operator std::vector<double>() const;
  operator std::list<double>() const;

  double at(int index) const;
  double& at(int index);
  int GetNumDimensions() const;
  double GetEuclideanNorm() const;
  // Usually this method called normalize
  EuclideanVector CreateUnitVector() const;

  friend bool operator==(const EuclideanVector& vector1, const EuclideanVector& vector2);
  friend bool operator!=(const EuclideanVector& vector1, const EuclideanVector& vector2);
  friend EuclideanVector operator+(const EuclideanVector& vector1, const EuclideanVector& vector2);
  friend EuclideanVector operator-(const EuclideanVector& vector1, const EuclideanVector& vector2);
  friend EuclideanVector operator*(const EuclideanVector& vector1, double num);  // scalar product
  friend EuclideanVector operator/(const EuclideanVector& vector1, double num);  // scalar product
  friend double operator*(const EuclideanVector& vector1,
                          const EuclideanVector& vector2);  // dot product
  friend std::ostream& operator<<(std::ostream& os, const EuclideanVector& vector);

 private:
  std::unique_ptr<double[]> magnitudes_;
  int numOfDimension_;
  void checkDimension(const EuclideanVector vector) const;
  void checkIndex(int index) const;
  void checkZeroDimension(const std::string vectorType) const;
  void checkInvalidDivision(double num) const;
  void checkIsZeroNorm() const;
};
#endif  // COMP6771_ASSIGNMENTS_EV_EUCLIDEAN_VECTOR_H_
