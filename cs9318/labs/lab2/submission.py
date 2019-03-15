## import modules here 
import pandas as pd
import numpy as np



################# Question 1 #################

def v_opt_dp(x, num_bins):# do not change the heading of the function
    res = pd.qcut(np.array(x), num_bins,retbins=True,)
    left=sorted(x[:-1])
    right=sorted(x[1:])
    bins = pd.IntervalIndex.from_arrays(left,right)
    print(bins)

    return res