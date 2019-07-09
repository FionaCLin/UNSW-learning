#include <cstdio>
#include <algorithm>

class Foo {
	public:
		Foo() { printf("Constructor\n"); }
		~Foo() { printf("Deconstructor\n"); }
};


template<typename T> class UniquePtr {
	public:
		UniquePtr (T* t) : ptr_(std::move(t)) { }
		~UniquePtr () { delete ptr_; }
	private:
		T * ptr_;
};


int main() {
	Foo *f = new Foo();
	{
		UniquePtr<Foo> * p = new UniquePtr<Foo>(f);
	}	

	printf("hello world\n");

	return 0;
}
