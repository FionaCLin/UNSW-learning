#include "assignments/ev/euclidean_vector.h"

EuclideanVector::~EuclideanVector() noexcept {}

// ++++++++++++++++++++++++++++++++++++constructors++++++++++++++++++++++++++++++++++++++
EuclideanVector::EuclideanVector(int i)
    : magnitudes_{std::make_unique<double[]>(i)}, numOfDimension_{i} {
  //  magnitudes_ = std::make_unique()
  for (auto j = 0; j < i; ++j) {
    magnitudes_[j] = 0;
  }
}

EuclideanVector::EuclideanVector(int i, double m)
    : magnitudes_{std::make_unique<double[]>(i)}, numOfDimension_{i} {
  for (auto j = 0; j < i; ++j) {
    magnitudes_[j] = m;
  }
}

EuclideanVector::EuclideanVector(std::vector<double>::const_iterator start,
                                 std::vector<double>::const_iterator end)
    : magnitudes_{std::make_unique<double[]>(end - start)}, numOfDimension_{
    (static_cast<int>(end - start))} {
  int i = 0;
  for (auto& it = start; it != end; ++it) {
    magnitudes_[i++] = *it;
  }
}
// copy
EuclideanVector::EuclideanVector(const EuclideanVector& a)
    : magnitudes_{std::make_unique<double[]>(a.numOfDimension_)},
      numOfDimension_{a.numOfDimension_} {
  for (auto j = 0; j < a.numOfDimension_; ++j) {
    this->magnitudes_[j] = a.magnitudes_[j];
  }
}

EuclideanVector::EuclideanVector(EuclideanVector&& a) noexcept
    : magnitudes_(std::move(a.magnitudes_)), numOfDimension_(std::exchange(a.numOfDimension_, 0)) {
}

// +++++++++++++++++++++++++++++++Operations++++++++++++++++++++++++++++++++++

// copy assignment operator
EuclideanVector& EuclideanVector::operator=(const EuclideanVector& a) {
  if (this == &a) {
    return *this;
  }
  this->magnitudes_ = std::make_unique<double[]>(a.numOfDimension_);
  for (auto j = 0; j < a.numOfDimension_; ++j) {
    this->magnitudes_[j] = a.magnitudes_[j];
  }
  this->numOfDimension_ = a.numOfDimension_;
  return *this;
}
// move assignment operator
EuclideanVector& EuclideanVector::operator=(EuclideanVector&& a) {
  this->numOfDimension_ = a.numOfDimension_;
  for (auto j = 0; j < a.numOfDimension_; ++j) {
    this->magnitudes_[j] = std::move(a.magnitudes_[j]);
  }
  this->numOfDimension_ = a.numOfDimension_;
  return *this;
}
// setting via []
double& EuclideanVector::operator[](int i) {
  return this->magnitudes_[i];
}

// getting via []
double EuclideanVector::operator[](int i) const {
  return this->magnitudes_[i];
}

EuclideanVector& EuclideanVector::operator+=(const EuclideanVector& ev) {
  // assert the dimensions
  for (auto j = 0; j < numOfDimension_; j++) {
    this->magnitudes_[j] += ev.magnitudes_[j];
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator-=(const EuclideanVector& ev) {
  // assert the dimensions
  for (auto j = 0; j < numOfDimension_; j++) {
    this->magnitudes_[j] -= ev.magnitudes_[j];
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator*=(const double& val) {
  // assert the dimensions
  for (auto j = 0; j < numOfDimension_; j++) {
    this->magnitudes_[j] *= val;
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator/=(const double& val) {
  // assert the dimensions
  for (auto j = 0; j < numOfDimension_; j++) {
    this->magnitudes_[j] /= val;
  }
  return *this;
}

EuclideanVector::operator std::vector<double>() {
  std::vector<double> vec;
  for (auto j = 0; j < numOfDimension_; j++) {
    vec.push_back(this->magnitudes_[j]);
  }
  return vec;
}
EuclideanVector::operator std::list<double>() {
  std::list<double> res;
  for (auto j = 0; j < numOfDimension_; j++) {
    res.push_back(this->magnitudes_[j]);
  }
  return res;
}
// +++++++++++++++++++++++++++++++++++++methods+++++++++++++++++++++++++++++++
double EuclideanVector::at(int i) {
  return magnitudes_[i];
}
int EuclideanVector::GetNumDimensions() {
  return numOfDimension_;
}
double EuclideanVector::GetEuclideanNorm() {
  double sum = 0;
  for (auto j = 0; j < numOfDimension_; ++j) {
    sum += (magnitudes_[j] * magnitudes_[j]);
  }
  return std::sqrt(sum);
}

EuclideanVector EuclideanVector::CreateUnitVector() {
  if (this->numOfDimension_ == 0) {
    throw EuclideanVectorError("EuclideanVector with no dimensions does not have a unit vector");
  }
  std::vector<double> l;
  for (auto j = 0; j < numOfDimension_; ++j) {
    l.push_back(magnitudes_[j] / this->GetEuclideanNorm());
  }
  return EuclideanVector{l.begin(), l.end()};
}

// +++++++++++++++++++++++++++++++++++++friends++++++++++++++++++++++++++++++++++++++
bool operator==(const EuclideanVector& v1, const EuclideanVector& v2) {
  if (v1.numOfDimension_ != v2.numOfDimension_) {
    return false;
  }
  for (auto j = 0; j < v1.numOfDimension_; ++j) {
    if (v1.magnitudes_[j] != v2.magnitudes_[j]) {
      return false;
    }
  }
  return true;
}
bool operator!=(const EuclideanVector& v1, const EuclideanVector& v2) {
  return !(v1 == v2);
}

EuclideanVector operator+(const EuclideanVector& v1, const EuclideanVector& v2) {
  EuclideanVector res = v1;
  res += v2;
  return res;
}
EuclideanVector operator-(const EuclideanVector& v1, const EuclideanVector& v2) {
  EuclideanVector res = v1;
  res -= v2;
  return res;
}
// dot product
double operator*(const EuclideanVector& v1, const EuclideanVector& v2) {
  //  if (v1.numOfDimension_ != v2.numOfDimension_) {
  ////    return new EuclideanVectorError("Dimensions of LHS(X) and RHS(Y) do not match");
  //  }
  double res = 0;
  for (auto j = 0; j < v1.numOfDimension_; ++j) {
    res += v1.magnitudes_[j] * v2.magnitudes_[j];
  }
  return res;
}
EuclideanVector operator*(const EuclideanVector& v1, const double& num) {
  EuclideanVector res = v1;
  res *= num;
  return res;
}
EuclideanVector operator/(const EuclideanVector& v1, const double& num) {
  EuclideanVector res = v1;
  res /= num;
  return res;
}

std::ostream& operator<<(std::ostream& os, const EuclideanVector& v) {
  os << "[ ";
  for (auto j = 0; j < v.numOfDimension_; ++j) {
    os << v.magnitudes_[j] << ' ';
  }
  os << ']';
  return os;
}
