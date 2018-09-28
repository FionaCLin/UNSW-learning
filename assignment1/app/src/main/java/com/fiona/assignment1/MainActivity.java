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

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Handler mHandler = new Handler();
    private double lastXPoint = 0;

    // Sensor
    private SensorManager mSensorManager;
    private Sensor mLight;


    private LineGraphSeries<DataPoint> series;
    private GraphView graph;
    private Viewport viewport;

    private LineGraphSeries<DataPoint> series2r;
    private LineGraphSeries<DataPoint> series2g;
    private LineGraphSeries<DataPoint> series2b;
    private GraphView graph2;
    private Viewport viewport2;
    private Random rnd = new Random();
    private float val = 0;
    private int troungCount = 0;

    private double [] rgb = {0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[]{});
        graph.addSeries(series);

        viewport = graph.getViewport();
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


        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        // TODO UPDATE the frequence as assignment spec required
        mSensorManager.registerListener(this, mLight, 10000);

        graph2 = (GraphView) findViewById(R.id.graph2);
        series2r = new LineGraphSeries<>(new DataPoint[]{});
        series2r.setColor(Color.RED);
        graph .addSeries(series2r);

        series2g = new LineGraphSeries<>(new DataPoint[]{});
        series2g.setColor(Color.GREEN);
        graph2.addSeries(series2g);

        series2b = new LineGraphSeries<>(new DataPoint[]{});
        series2b.setColor(Color.BLUE);
        graph2.addSeries(series2b);

        viewport2 = graph2.getViewport();
        graph2.onDataChanged(false, false);

        viewport2.setXAxisBoundsManual(true);
        viewport2.setMinX(lastXPoint - 5);
        viewport2.setMaxX(lastXPoint + 5);

        viewport2.setYAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        viewport2.setMinY(0);
        viewport2.setMaxY(40000);

        viewport2.setScrollable(true);
        viewport2.setScalable(true);
        viewport2.setScalableY(true);

        updateGraph();
    }

    private void updateGraph() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint += 0.01;

                System.out.println(lastXPoint + "--" + val);

                series.appendData(new DataPoint(lastXPoint, val), false, 1000);
                series2r.appendData(new DataPoint(lastXPoint, rgb[2]), false, 1000);

                viewport.setMinX(lastXPoint - 5);
                viewport.setMaxX(lastXPoint + 0.25);


                series2b.appendData(new DataPoint(lastXPoint, rgb[0]), false, 1000);
                series2g.appendData(new DataPoint(lastXPoint, rgb[1]), false, 1000);

                viewport2.setMinX(lastXPoint - 5);
                viewport2.setMaxX(lastXPoint + .25);

                updateGraph();
            }
        }, 10);
        // 0.1 second, sensor sample data period 0.01
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

            int len =  sensorEvent.values.length;
            this.troungCount = len;
            String tCount = "Troung Count: " + len + " on " + lastXPoint + "\n"+sensorEvent.values[0]
                    +" "+sensorEvent.values[1] + " "+sensorEvent.values[2];


            this.rgb[0] = sensorEvent.values[0];
            this.rgb[1] = sensorEvent.values[1];
            this.rgb[2] = sensorEvent.values[2];

            TextView troungCount = findViewById(R.id.troungCount);
            troungCount.setText(tCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
