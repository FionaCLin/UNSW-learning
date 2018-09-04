# -*- coding: utf-8 -*-

import pandas as pd
import numpy as np
from sklearn.linear_model import  LogisticRegression
from sklearn.svm import SVC

def csv_data(filename):
    df = pd.read_csv(filename)
    #data = df.as_matrix(columns = ['Ambiant Temp C', 'Object Temp C'])
    #data = df.as_matrix(columns = [ 'Object Temp C'])
    data = df.as_matrix(columns = [ 'Brightness']) 
    return data



################################################################################
if __name__ == "__main__":

    # Make binary data from two reading files

	#Obtain the brightness samples obtained at the bright location
    X_bright = csv_data('record-bright.csv') 
    y_bright = np.ones(X_hot.shape[0])

	#Obtain the brightness samples obtained at the dark location
    X_dark = csv_data('record-dark.csv')
    y_dark = np.zeros(X_cold.shape[0])

    X = np.concatenate((X_bright, X_dark), axis=0)
    y = np.concatenate((y_bright, y_dark), axis=0)
	
	# Now complete this function! 
	# Train and tuning different classifiers, and print their accuracy.

   
    

    
