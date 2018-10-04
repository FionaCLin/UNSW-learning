package com.unswcs4336.lin.fiona.assignment_task2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import static android.content.ContentValues.TAG;

public class LightSensorData {
    final static int SUM = 0, MEAN = 1, SD = 2, MEDIAN = 3;

    private TreeMap<Double, Double> data;
    double[] stats = new double[4];

    //constructors
    public LightSensorData() {
        this.data = new TreeMap<Double, Double>();
    }

    public Double getData(Double key) {
        return data.get(key);
    }

    public Map.Entry<Double, Double> getLastData() {
        Map.Entry<Double, Double> res = null;
        Iterator<Map.Entry<Double, Double>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            res = it.next();
        }
        return res;
    }

    public void putData(double x, double y) {
        this.data.put(x, y);
    }

    public int getTroungCount(double end, double rate) {

        int troungCount = 0;
        ArrayList<Map.Entry<Double, Double>> dset = new ArrayList<>();
        for (Map.Entry<Double, Double> e : data.entrySet()) {
            if (e.getKey() > end - 5 && e.getKey() <= end) {
                dset.add(e);
            }
        }



        double[] sort = new double[dset.size()];
        // get SD
        int i = 0;
        for (Map.Entry<Double, Double> num : dset) {
            sort[i++] = num.getValue();
        }

        Arrays.sort(sort);
        int median = dset.size() / 2;

        stats[MEDIAN] = sort[median--];

        if (dset.size() % 2 == 0) {
            stats[MEDIAN] += sort[median];
            stats[MEDIAN] /= 2;
        }


        // calculate threshold
        double threshold = rate * stats[MEDIAN];
        double[] filter = new double[dset.size()];
        // filter signal
        i = 0;
        for (Map.Entry<Double, Double> num : dset) {
            if (num.getValue() < threshold) {
                filter[i] = -1;
            } else {
                filter[i] = 0;
            }
            i++;
        }

        // count troungs
        for (int j = 1, ii = dset.size(); j < ii; j++) {
            if (filter[j - 1] > filter[j]) {
                troungCount++;
            }
        }

        return troungCount;
    }

}
