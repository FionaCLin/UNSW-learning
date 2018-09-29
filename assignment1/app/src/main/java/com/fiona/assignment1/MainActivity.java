package com.fiona.assignment1;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Handler mHandler = new Handler();
    private Handler mHandler2 = new Handler();
    private double lastXPoint = 0;

    // Sensor
    private SensorManager mSensorManager;
    private Sensor mLight;


    private LineGraphSeries<DataPoint> series;
    private GraphView graph;

    private LineGraphSeries<DataPoint> fliteredSeries;
    private GraphView fliteredGraph;
    private double val = 0;
    private int troungCount = 0;
    private boolean troug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[]{});
        initGraphView(series, graph);


//        fliteredGraph = (GraphView) findViewById(R.id.graph2);
//        fliteredSeries = new LineGraphSeries<>(new DataPoint[]{});
//        initGraphView(fliteredSeries, fliteredGraph);


        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        // TODO assignment spec required plot the data of last 5 sec no spec for the frequence
        // should this be 10000 microsecond ??
        mSensorManager.registerListener(this, mLight, 10000);


        updateGraph();
        updateTroungCount();
    }

    private void initGraphView(LineGraphSeries series, GraphView graph) {
        graph.addSeries(series);

        Viewport viewport = graph.getViewport();
        graph.onDataChanged(false, false);

        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(lastXPoint - 5);
        viewport.setMaxX(lastXPoint + 5);

        viewport.setYAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        viewport.setMinY(0);
        viewport.setMaxY(40000);
        viewport.setScrollable(true);
        viewport.setScalable(true);
        viewport.setScalableY(true);
    }

    private void updateGraph() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint += 0.1;


                System.out.println(lastXPoint + "--" + val);

                series.appendData(new DataPoint(lastXPoint, val), false, 550);

                graph.getViewport().setMinX(lastXPoint - 5);
                graph.getViewport().setMaxX(lastXPoint + 0.15);


                updateGraph();
            }
        }, 100);
        // 0.1 second, sensor sample data period 0.01
    }

    private void updateTroungCount() {
        mHandler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTroungCount();
                if (lastXPoint < 5) {
                    return;
                }
                Iterator<DataPoint> d = series.getValues(lastXPoint - 1, lastXPoint);
                double [] stats = StatisticUtinity.getStats(d);


                if (Math.abs(val - stats[3]) > stats[4]) {
                    if (val >= stats[1]) {
                        if (troug) troug = false;
//                        fliteredSeries.appendData(new DataPoint(lastXPoint, 1), false, 550);
                    } else if(val <= stats[0]) {
//                        fliteredSeries.appendData(new DataPoint(lastXPoint, -1), false, 550);
                        if (troug == false) {
                            troungCount++;
                            troug = true;
                        }
                    } else {
                        if(troug) troug = false;
//                        fliteredSeries.appendData(new DataPoint(lastXPoint, 0), false, 550);
                    }
                } else {
                    if(troug) troug = false;
//                    fliteredSeries.appendData(new DataPoint(lastXPoint, 0), false, 550);
                }

//                fliteredGraph.getViewport().setMinX(lastXPoint - 5);
//                fliteredGraph.getViewport().setMaxX(lastXPoint + 0.15);

            }
        }, 100);
        // 1 second, sensor sample data period 0.01
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
//            Sensor.TYPE_LIGHT:
//            values[0]: Ambient light level in SI lux units

            this.val = sensorEvent.values[0];
            String dis = "Light Sensor: " + this.val + " lux on " + lastXPoint;
            TextView sensorVal = findViewById(R.id.sensorVal);
            sensorVal.setText(dis);
            String tCount = " Troung Count: " + troungCount + " on " + lastXPoint + "\n" + sensorEvent.values[0]
                    + " " + sensorEvent.values[1] + " " + sensorEvent.values[2];


            TextView troungCount = findViewById(R.id.troungCount);
            troungCount.setText(tCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
