#include "assignments/ev/euclidean_vector.h"

EuclideanVector::~EuclideanVector() noexcept {}

// ++++++++++++++++++++++++++++++++++++constructors++++++++++++++++++++++++++++++++++++++
EuclideanVector::EuclideanVector(int numOfDimension) : EuclideanVector(numOfDimension, 0) {}

EuclideanVector::EuclideanVector(int numOfDimension, double magnitudes)
  : magnitudes_(std::make_unique<double[]>(numOfDimension)), numOfDimension_(numOfDimension) {
  for (auto j = 0; j < numOfDimension; ++j) {
    magnitudes_[j] = magnitudes;
  }
}

EuclideanVector::EuclideanVector(std::vector<double>::const_iterator start,
                                 std::vector<double>::const_iterator end)
  : magnitudes_(std::make_unique<double[]>(end - start)),
    numOfDimension_(static_cast<int>(end - start)) {
  std::copy(start, end, &magnitudes_[0]);
}
// copy
EuclideanVector::EuclideanVector(const EuclideanVector& a)
  : magnitudes_(std::make_unique<double[]>(a.numOfDimension_)), numOfDimension_(a.numOfDimension_) {
  for (auto j = 0; j < a.numOfDimension_; ++j) {
    magnitudes_[j] = a.magnitudes_[j];
  }
}

EuclideanVector::EuclideanVector(EuclideanVector&& a) noexcept
  : magnitudes_(std::move(a.magnitudes_)), numOfDimension_(std::exchange(a.numOfDimension_, 0)) {}

// +++++++++++++++++++++++++++++++Operations++++++++++++++++++++++++++++++++++

// copy assignment operator
EuclideanVector& EuclideanVector::operator=(const EuclideanVector& a) {
  if (this == &a) {
    return *this;
  }
  magnitudes_ = std::make_unique<double[]>(a.numOfDimension_);
  for (auto j = 0; j < a.numOfDimension_; ++j) {
    magnitudes_[j] = a.magnitudes_[j];
  }
  numOfDimension_ = a.numOfDimension_;
  return *this;
}
// move assignment operator
EuclideanVector& EuclideanVector::operator=(EuclideanVector&& a) {
  magnitudes_ = std::move(a.magnitudes_);
  numOfDimension_ = a.numOfDimension_;
  a.numOfDimension_ = 0;
  return *this;
}

// setting via []
double& EuclideanVector::operator[](int i) {
  assert(i >= 0 || i < numOfDimension_);
  return magnitudes_[i];
}

// getting via []
double EuclideanVector::operator[](int i) const {
  assert(i >= 0 || i < numOfDimension_);
  return magnitudes_[i];
}

EuclideanVector& EuclideanVector::operator+=(const EuclideanVector& ev) {
  checkDimension(ev);
  // assert the dimensions
  for (auto j = 0; j < numOfDimension_; j++) {
    magnitudes_[j] += ev.magnitudes_[j];
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator-=(const EuclideanVector& ev) {
  checkDimension(ev);
  // assert the dimensions
  for (auto j = 0; j < numOfDimension_; j++) {
    magnitudes_[j] -= ev.magnitudes_[j];
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator*=(double val) {
  for (auto j = 0; j < numOfDimension_; j++) {
    magnitudes_[j] *= val;
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator/=(double val) {
  checkInvalidDivision(val);
  double inverse = 1 / val;
  for (auto j = 0; j < numOfDimension_; j++) {
    magnitudes_[j] *= inverse;
  }
  return *this;
}

EuclideanVector::operator std::vector<double>() const {
  std::vector<double> vec(numOfDimension_);
  for (auto j = 0; j < numOfDimension_; j++) {
    vec[j] = magnitudes_[j];
  }
  return vec;
}
EuclideanVector::operator std::list<double>() const {
  std::list<double> res(0);
  for (auto j = 0; j < numOfDimension_; j++) {
    res.push_back(magnitudes_[j]);
  }
  return res;
}
// +++++++++++++++++++++++++++++++++++++methods+++++++++++++++++++++++++++++++
double EuclideanVector::at(int i) const {
  checkIndex(i);
  return magnitudes_[i];
}
double& EuclideanVector::at(int i) {
  checkIndex(i);
  return magnitudes_[i];
}
int EuclideanVector::GetNumDimensions() const {
  return numOfDimension_;
}
double EuclideanVector::GetEuclideanNorm() const {
  checkZeroDimension("norm");
  double sum = 0;
  for (auto j = 0; j < numOfDimension_; ++j) {
    sum = std::hypot(sum, magnitudes_[j]);
  }
  return sum;
}

EuclideanVector EuclideanVector::CreateUnitVector() const {
  checkZeroDimension("unit vector");
  this->checkIsZeroNorm();
  return *this / GetEuclideanNorm();
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
  v1.checkDimension(v2);
  EuclideanVector res = v1;
  res += v2;
  return res;
}
EuclideanVector operator-(const EuclideanVector& v1, const EuclideanVector& v2) {
  v1.checkDimension(v2);
  EuclideanVector res = v1;
  res -= v2;
  return res;
}
// dot product
double operator*(const EuclideanVector& v1, const EuclideanVector& v2) {
  v1.checkDimension(v2);
  double res = 0;
  for (auto j = 0; j < v1.numOfDimension_; ++j) {
    res += v1.magnitudes_[j] * v2.magnitudes_[j];
  }
  return res;
}
EuclideanVector operator*(const EuclideanVector& v1, double num) {
  EuclideanVector res = v1;
  res *= num;
  return res;
}
EuclideanVector operator/(const EuclideanVector& v1, double num) {
  v1.checkInvalidDivision(num);
  EuclideanVector res = v1;
  res /= num;
  return res;
}

std::ostream& operator<<(std::ostream& os, const EuclideanVector& v) {
  os << "[";
  for (auto j = 0; j < v.numOfDimension_; ++j) {
    os << v.magnitudes_[j];
    if (j + 1 != v.numOfDimension_) {
      os << ' ';
    }
  }
  os << ']';
  return os;
}
void EuclideanVector::checkInvalidDivision(double num) const {
  if (num == 0) {
    throw EuclideanVectorError("Invalid vector division by 0");
  }
}
void EuclideanVector::checkZeroDimension(const std::string vectorType) const {
  if (numOfDimension_ == 0) {
    throw EuclideanVectorError("EuclideanVector with no dimensions does not have a " + vectorType);
  }
}

void EuclideanVector::checkDimension(EuclideanVector v) const {
  if (numOfDimension_ != v.numOfDimension_) {
    throw EuclideanVectorError("Dimensions of LHS(X) and RHS(Y) do not match");
  }
}
void EuclideanVector::checkIndex(int index) const {
  if (index >= numOfDimension_ || index < 0) {
    throw EuclideanVectorError("Index " + std::to_string(index) +
                               " is not valid for this EuclideanVector object");
  }
}
void EuclideanVector::checkIsZeroNorm() const {
  if (GetEuclideanNorm() == 0) {
    throw EuclideanVectorError(
        "EuclideanVector with euclidean normal of 0 does not have a unit vector");
  }
}
