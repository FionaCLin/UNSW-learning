#include "assignments/ev/euclidean_vector.h"

EuclideanVector::~EuclideanVector() noexcept = default;

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
EuclideanVector::EuclideanVector(const EuclideanVector& vector)
  : magnitudes_(std::make_unique<double[]>(vector.numOfDimension_)),
    numOfDimension_(vector.numOfDimension_) {
  for (auto j = 0; j < vector.numOfDimension_; ++j) {
    magnitudes_[j] = vector.magnitudes_[j];
  }
}

EuclideanVector::EuclideanVector(EuclideanVector&& vector) noexcept
  : magnitudes_(std::move(vector.magnitudes_)),
    numOfDimension_(std::exchange(vector.numOfDimension_, 0)) {}

// +++++++++++++++++++++++++++++++Operations++++++++++++++++++++++++++++++++++

// copy assignment operator
EuclideanVector& EuclideanVector::operator=(const EuclideanVector& vector) {
  if (this == &vector) {
    return *this;
  }
  magnitudes_ = std::make_unique<double[]>(vector.numOfDimension_);
  for (auto j = 0; j < vector.numOfDimension_; ++j) {
    magnitudes_[j] = vector.magnitudes_[j];
  }
  numOfDimension_ = vector.numOfDimension_;
  return *this;
}
// move assignment operator
EuclideanVector& EuclideanVector::operator=(EuclideanVector&& vector) {
  magnitudes_ = std::move(vector.magnitudes_);
  numOfDimension_ = vector.numOfDimension_;
  vector.numOfDimension_ = 0;
  return *this;
}

// setting via []
double& EuclideanVector::operator[](int index) {
  assert(index >= 0 && index < numOfDimension_);
  return magnitudes_[index];
}

// getting via []
double EuclideanVector::operator[](int index) const {
  assert(index >= 0 && index < numOfDimension_);
  return magnitudes_[index];
}

EuclideanVector& EuclideanVector::operator+=(const EuclideanVector& vector) {
  checkDimension(vector);
  // assert the dimensions
  for (auto j = 0; j < numOfDimension_; j++) {
    magnitudes_[j] += vector.magnitudes_[j];
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator-=(const EuclideanVector& vector) {
  checkDimension(vector);
  // assert the dimensions
  for (auto j = 0; j < numOfDimension_; j++) {
    magnitudes_[j] -= vector.magnitudes_[j];
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator*=(double num) {
  for (auto j = 0; j < numOfDimension_; j++) {
    magnitudes_[j] *= num;
  }
  return *this;
}
EuclideanVector& EuclideanVector::operator/=(double num) {
  checkInvalidDivision(num);
  double inverse = 1 / num;
  for (auto j = 0; j < numOfDimension_; j++) {
    magnitudes_[j] *= inverse;
  }
  return *this;
}

EuclideanVector::operator std::vector<double>() const {
  std::vector<double> vector(numOfDimension_);
  for (auto j = 0; j < numOfDimension_; j++) {
    vector[j] = magnitudes_[j];
  }
  return vector;
}
EuclideanVector::operator std::list<double>() const {
  std::list<double> res(0);
  for (auto j = 0; j < numOfDimension_; j++) {
    res.push_back(magnitudes_[j]);
  }
  return res;
}
// +++++++++++++++++++++++++++++++++++++methods+++++++++++++++++++++++++++++++
double EuclideanVector::at(int index) const {
  checkIndex(index);
  return magnitudes_[index];
}
double& EuclideanVector::at(int index) {
  checkIndex(index);
  return magnitudes_[index];
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
  checkIsZeroNorm();
  return *this / GetEuclideanNorm();
}

// +++++++++++++++++++++++++++++++++++++friends++++++++++++++++++++++++++++++++++++++
bool operator==(const EuclideanVector& vector1, const EuclideanVector& vector2) {
  if (vector1.numOfDimension_ != vector2.numOfDimension_) {
    return false;
  }
  for (auto j = 0; j < vector1.numOfDimension_; ++j) {
    if (vector1.magnitudes_[j] != vector2.magnitudes_[j]) {
      return false;
    }
  }
  return true;
}
bool operator!=(const EuclideanVector& vector1, const EuclideanVector& vector2) {
  return !(vector1 == vector2);
}

EuclideanVector operator+(const EuclideanVector& vector1, const EuclideanVector& vector2) {
  vector1.checkDimension(vector2);
  EuclideanVector res = vector1;
  res += vector2;
  return res;
}
EuclideanVector operator-(const EuclideanVector& vector1, const EuclideanVector& vector2) {
  vector1.checkDimension(vector2);
  EuclideanVector res = vector1;
  res -= vector2;
  return res;
}
// dot product
double operator*(const EuclideanVector& vector1, const EuclideanVector& vector2) {
  vector1.checkDimension(vector2);
  double res = 0;
  for (auto j = 0; j < vector1.numOfDimension_; ++j) {
    res += vector1.magnitudes_[j] * vector2.magnitudes_[j];
  }
  return res;
}
EuclideanVector operator*(const EuclideanVector& vector, double num) {
  EuclideanVector res = vector;
  res *= num;
  return res;
}
EuclideanVector operator/(const EuclideanVector& vector, double num) {
  vector.checkInvalidDivision(num);
  EuclideanVector res = vector;
  res /= num;
  return res;
}

std::ostream& operator<<(std::ostream& os, const EuclideanVector& vector) {
  os << "[";
  for (auto j = 0; j < vector.numOfDimension_; ++j) {
    os << vector.magnitudes_[j];
    if (j + 1 != vector.numOfDimension_) {
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

void EuclideanVector::checkDimension(EuclideanVector vector) const {
  if (numOfDimension_ != vector.numOfDimension_) {
    throw EuclideanVectorError("Dimensions of LHS(" + std::to_string(numOfDimension_) +
                               ") and RHS(" + std::to_string(vector.numOfDimension_) +
                               ") do not match");
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
