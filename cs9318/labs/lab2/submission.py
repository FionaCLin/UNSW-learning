## import modules here 
import pandas as pd
import numpy as np



################# Question 1 #################

def v_opt_dp(x, num_bins):
    matrix = [x[:] for x in [[-1] * len(x)] * num_bins]  
    path = [x[:] for x in [[-1] * len(x)] * num_bins] 
    bins = []
    minSSE = np.NAN               
    for i in range(1,num_bins+1):
        for j in range(len(x)-1,num_bins-i-1,-1):
            minIndex = -1
            
            ## filter out the invalid cells
            if i <= len(x[j:]): 
                ## get S_j to calculate sse + opt(s_j+1, i-1)<- bin = i-1
                s=x[j:] 
                sse = np.sum(np.array([(i-np.mean(np.array(s)))**2 for i in s]))
                if i == 1:
                #level 1 bin just put the opt(s_j+1,1)
                #because only 1 bin so no need to look up table
                    matrix[0][j] = sse 
                else:
                #level 2+ bins need to look up table and update the min sse and its optopt(s_j+1, i-1)
                    for k in range(len(s)-1,0,-1):
                        #start select minimum sse + opt(s_j, b-1)
                        sse = np.sum(np.array([(e-np.mean(np.array(s[:k])))**2 for e in s[:k]]))
                        if  matrix[i-2][j+k] > 0:
                            sse += matrix[i-2][j+k]
                        if np.isnan(minSSE):
                            minSSE = sse
                        elif minSSE > sse :
                            minSSE = sse
                        if sse == minSSE:
                        # update the minIndex if it is same as minSSE
                            minIndex = j+k
                    # note for each j, it needs to record the min s_j
                    matrix[i-1][j] = minSSE
                    path[i-1][j]=[j,minIndex]
                    minSSE = np.NAN
                    
    pick = 0
    for row in range(len(path)-1,0,-1):
        b = path[row][pick][0]
        pick = path[row][pick][1]
        bins.append(x[b:pick])
    bins.append(x[pick:])
    return (matrix, bins)