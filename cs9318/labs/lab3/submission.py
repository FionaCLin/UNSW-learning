import numpy as np


def sigmoid(weights, X):  # logistic(sigmoid) function
    return 1.0/(1 + np.exp(-np.dot(X, weights.T)))


def log_gradient(weights, X, y):  # logistic gradient function
    first_deriv = sigmoid(weights, X) - y.reshape(X.shape[0], -1)
    final_deriv = np.dot(first_deriv.T, X)
    return final_deriv


def cost_func(weights, X, y):  # cost function, J
    log_func_v = sigmoid(weights, X)
    y = np.squeeze(y)
    step1 = y * np.log(log_func_v)
    step2 = (1 - y) * np.log(1 - log_func_v)
    final = -step1 - step2
    return np.mean(final)


def grad_desc(X, y, weights, lr=.01, converge_change=.001):  # gradient descent function
    cost = cost_func(weights, X, y)
    change_cost = 1
    num_iter = 1

    while(num_iter < converge_change):
        old_cost = cost
        weights = weights - (lr * log_gradient(weights, X, y))
        cost = cost_func(weights, X, y)
        change_cost = old_cost - cost
        num_iter += 1
    return weights


def logistic_regression(data, labels, weights, num_epochs, learning_rate):
    # do not change the heading of the function

    X = data[:, :]

    # stacking columns wth all ones in feature matrix
    X = np.hstack((np.matrix(np.ones(X.shape[0])).T, X))

    weights = np.matrix(weights)

    weights = grad_desc(X, labels, weights, learning_rate, num_epochs)

    np.set_printoptions(precision=8)
    weights = np.squeeze(np.asarray(weights))

    return weights
