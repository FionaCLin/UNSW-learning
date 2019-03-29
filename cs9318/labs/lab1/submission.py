## import modules here 
import math

################# Question 0 #################

def add(a, b): # do not change the heading of the function
    return a + b


################# Question 1 #################

def nsqrt(x): # do not change the heading of the function
    if x < 0:
        return None
    lo = 0
    hi = x
    root = 0
    while root*root != x:
        root = math.floor((hi+lo)/2)
        if root != hi and root*root > x:
            hi = root
        elif root != lo and root*root < x:
            lo = root
        elif root*root == x:
            return root
        else:
            break
    return root

################# Question 2 #################


# x_0: initial guess
# EPSILON: stop when abs(x - x_new) < EPSILON
# MAX_ITER: maximum number of iterations

## NOTE: you must use the default values of the above parameters, do not change them

def find_root(f, fprime, x_0=1.0, EPSILON = 1E-7, MAX_ITER = 1000): # do not change the heading of the function
    x_n = x_0
    for n in range(0, MAX_ITER):
        fx = f(x_n)
        if abs(fx) < EPSILON:
#             print('Found solution after',n,'iterations.')
            return x_n
        Dfx = fprime(x_n)
        if Dfx == 0:
#             print('Zero derivative. No solution found.')
            return None
        x_n = x_n - fx/Dfx
#     print('Exceeded maximum iterations. No solution found.')
    return None

################# Question 3 #################

class Tree(object):
    def __init__(self, name='ROOT', children=None):
        self.name = name
        self.children = []
        if children is not None:
            for child in children:
                self.add_child(child)
    def __repr__(self):
        return self.name
    def add_child(self, node):
        assert isinstance(node, Tree)
        self.children.append(node)

def make_tree(tokens): # do not change the heading of the function
    root = Tree(tokens[0])
    child = root
    ancestors = []
    for i in range(1, len(tokens)):
        if tokens[i] == '[':
            ancestors.append(root)
            root = child
        elif tokens[i] == ']':
            root = ancestors.pop()
        else:
            child = Tree(tokens[i])
            root.add_child(child)
        i += 1
    return root

def max_depth(root): # do not change the heading of the function
    if root is None:
        return 0
    elif root.children is None or len(root.children) == 0:
        return 1
    else:
        depths = []
        for c in root.children:
            depths.append(max_depth(c))
        return (1+max(depths))
