package com.fiona.assignment1;

import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StatisticUtinity {
    public static double calculateSD(Iterator<DataPoint> it) {
        double sum = 0.0, standardDeviation = 0.0;
        double[] stats = new double[5];
        ArrayList<Double> numArray = new ArrayList<>();

        while (it.hasNext()) {
            numArray.add(it.next().getY());
        }
        for (double num : numArray) {
            sum += num;
            System.out.println(num + "  " + numArray.size() + " " + sum);
        }

        double mean = sum / numArray.size();

        for (double num : numArray) {
            standardDeviation += Math.pow(num - mean, 2);
            System.out.println(num + "  " + numArray.size() + " sd " + standardDeviation);
        }

        return Math.sqrt(standardDeviation / numArray.size());
    }

    public static double[] getStats(Iterator<DataPoint> it) {
        double sum = 0.0, standardDeviation = 0.0;
        double[] stats = new double[5];
        ArrayList<Double> numArray = new ArrayList<>();

        while (it.hasNext()) {
            numArray.add(it.next().getY());
        }
        double min = numArray.get(0);
        double max = min;
        for (double num : numArray) {
            if (min > num) min = num;
            if (max < num) max = num;
            sum += num;
            System.out.println(num + "  " + numArray.size() + " " + sum);
        }

        double mean = sum / numArray.size();

        for (double num : numArray) {
            standardDeviation += Math.pow(num - mean, 2);
            System.out.println(num + "  " + numArray.size() + " sd " + standardDeviation);
        }
        stats[0] = min;
        stats[1] = max;
        stats[2] = sum;
        stats[3] = mean;
        stats[4] = Math.sqrt(standardDeviation / numArray.size());
        return stats;
    }

    public static double calculateMean(Iterator<DataPoint> d) {
        double sum = 0.0;
        ArrayList<Double> numArray = new ArrayList<>();

        while (d.hasNext()) {
            numArray.add(d.next().getY());
        }

        for (double num : numArray) {
            sum += num;
            System.out.println(num + "  " + numArray.size() + " " + sum);
        }
        return sum / numArray.size();
    }
}
