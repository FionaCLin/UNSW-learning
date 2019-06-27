#include "assignments/ev/euclidean_vector.h"

#include <algorithm>  // Look at these - they are helpful https://en.cppreference.com/w/cpp/algorithm
#include <iostream>
#include <cmath>

//
//// TODO(you): Include header guards
//
//class EuclideanVectorError : public std::exception {
// public:
//  explicit EuclideanVectorError(const std::string& what) : what_(what) {}
//  const char* what() const noexcept{ return what_.c_str(); }
// private:
//  std::string what_;
//};


// ++++++++++++++++++++++++++++++++++++constructors++++++++++++++++++++++++++++++++++++++
EuclideanVector::EuclideanVector(int i): magnitudes_{new double[i]}, size_{i}  {
  for (auto j = 0; j < i; ++j) {
    magnitudes_[j] = 0;
  }

}

EuclideanVector::EuclideanVector(int i, double m) : magnitudes_{new double[i]}, size_{i}  {
  for (auto j = 0; j < i; ++j) {
    magnitudes_[j] = m;
    std::cout << j << "->" << magnitudes_[j] << std::endl;
  }
}

EuclideanVector::EuclideanVector(std::vector<double>::const_iterator start, std::vector<double>::const_iterator end)  : magnitudes_{new double[end-start]}, size_{((int)(end-start))}  {
  int i = 0;
  for (auto& it = start; it != end; ++it) {
    magnitudes_[i++] = *it;
  }
}

// +++++++++++++++++++++++++++++++Operations++++++++++++++++++++++++++++++++++
// Copy Assignment	operator=(const EuclideanVector&)	A copy assignment operator overload
//a = b;
//N/A

// Move Assignment	operator=(EuclideanVector&&)	A move assignment operator
//    a = std::move(b);
//N/A

// Subscript	operator[]	Allows to get and set the value in a given dimension of the Euclidean Vector. Hint: you may need two overloaded functions to achieve this requirement.
//Note: It's a requirement you use asserts to ensure the index passed is valid.
//double a {b[1]};
//b[2] = 3.0;
//N/A

// Addition	operator+=
//    For adding vectors of the same dimension.
//a += b;
//Given: X = a.GetNumDimensions(), Y = b.GetNumDimensions() When: X != Y
//0: "Dimensions of LHS(X) and RHS(Y) do not match"

//Subtraction	operator-=
//For subtracting vectors of the same dimension.
//a -= b;
//Given: X = a.GetNumDimensions(), Y = b.GetNumDimensions() When: X != Y
//    Throw: "Dimensions of LHS(X) and RHS(Y) do not match"


//Multiplication	operator*=
//For scalar multiplication, e.g. [1 2] * 3 = [3 6]
//a *= 3;
//N/A

//    Division	operator/=
//    For scalar division, e.g. [3 6] / 2 = [1.5 3]
//a /= 4;
//When: b == 0
//Throw: "Invalid vector division by 0"

//Vector Type Conversion
//explicit operator std::vector<double>()	Operators for type casting to a std::vector
//    EucilideanVector a;
//std::vector<double> vf = a;
//N/A

// +++++++++++++++++++++++++++++++++++++methods++++++++++++++++++++++++++++++++++++++
double EuclideanVector::at(int i) {
  return magnitudes_[i];
}
int EuclideanVector::GetNumDimensions() {
  return size_;
}
double EuclideanVector::GetEuclideanNorm() {
  double sum = 0;
  for (auto j = 0; j < size_; ++j) {
    sum += (magnitudes_[j]*magnitudes_[j]);
  }
  return std::sqrt(sum);
}

EuclideanVector EuclideanVector::CreateUnitVector() {
  if(this->size_==0){
    throw "EuclideanVector with no dimensions does not have a unit vector";
  }
  std::vector<double> l;
  for (auto j = 0; j < size_; ++j) {
    l.push_back(magnitudes_[j]/this->GetEuclideanNorm());
  }
  return EuclideanVector{l.begin(), l.end()};
}


// +++++++++++++++++++++++++++++++++++++friends++++++++++++++++++++++++++++++++++++++

//Equal	operator==	True if the two vectors are equal in the number of dimensions and the magnitude in each dimension is equal.
//a == b;
//N/A

// Not Equal	operator!=	True if the two vectors are not equal in the number of dimensions or the magnitude in each dimension is not equal.
//a != b;
//N/A


// Addition	operator+
//    For adding vectors of the same dimension.
//a = b + c;
//Given: X = a.GetNumDimensions(), Y = b.GetNumDimensions() When: X != Y
//    Throw: "Dimensions of LHS(X) and RHS(Y) do not match"

//Subtraction	operator-
//For substracting vectors of the same dimension.
//a = b - c;
//Given: X = a.GetNumDimensions(), Y = b.GetNumDimensions() When: X != Y
//    Throw: "Dimensions of LHS(X) and RHS(Y) do not match"


//Multiplication	operator*	For dot-product multiplication, returns a double. E.g., [1 2] * [3 4] = 1 * 3 + 2 * 4 = 11
//double c {a * b};
//Given: X = a.GetNumDimensions(), Y = b.GetNumDimensions() When: X != Y
//    Throw: "Dimensions of LHS(X) and RHS(Y) do not match"

//Multiply	operator*	For scalar multiplication, e.g. [1 2] * 3 = 3 * [1 2] = [3 6]. Hint: you'll obviously need two methods, as the scalar can be either side of the vector.
//(1) a = b * 3;
//(2) a = 3 * b;
//N/A
//    Divide	operator/	For scalar division, e.g. [3 6] / 2 = [1.5 3]
//a = b / 4;
//When: b == 0
//Throw: "Invalid vector division by 0"

//Output Stream	operator<<	Prints out the magnitude in each dimension of the Euclidean Vector (surrounded by [ and ]), e.g. for a 3-dimensional vector: [1 2 3]
//std::cout << a;
//N/A


std::ostream& operator<<(std::ostream& os, const EuclideanVector& v) {
  os << "[ ";
  for (auto j = 0; j < v.size_; ++j) {
    os << v.magnitudes_[j] << ' ';
  }
  os << ']'<< std::endl;
  return os;
}