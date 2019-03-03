package com.fionalin.guesturecontrolmusicplayer;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class LightSensorData {

    private TreeMap<Double, Double> data;
    private double median;
    private double duration;

    //constructors
    public LightSensorData() {
        this.data = new TreeMap<Double, Double>();
    }

    //constructors
    public LightSensorData(int duration) {
        this.duration = duration;
        this.data = new TreeMap<Double, Double>();
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
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
            if (e.getKey() > end - duration && e.getKey() <= end) {
                dset.add(e);
            }
        }

        double[] sort = new double[dset.size()];
        //sort the data and find the median
        int i = 0;
        for (Map.Entry<Double, Double> num : dset) {
            sort[i++] = num.getValue();
        }

        Arrays.sort(sort);
        int median = dset.size() / 2;

        this.median = sort[median--];

        if (dset.size() % 2 == 0) {
            this.median += sort[median];
            this.median /= 2;
        }


        // calculate threshold
        double threshold = rate * this.median;
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
