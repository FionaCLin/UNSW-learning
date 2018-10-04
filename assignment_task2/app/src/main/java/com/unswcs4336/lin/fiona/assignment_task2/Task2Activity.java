package com.unswcs4336.lin.fiona.assignment_task2;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;


import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Task2Activity extends AppCompatActivity implements SensorEventListener {

    private Handler mHandler = new Handler();

    // Sensor
    private SensorManager mSensorManager;
    private Sensor mLight;

    private GraphView graph;
    private LineGraphSeries<DataPoint> series;

    private int troungCount = 0;


    private LightSensorData data = new LightSensorData();
    private long from = System.currentTimeMillis();
    private double lastXPoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[]{});
        initGraphView(series, graph);


        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_UI);
        update();
    }


    private void update() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                updateGraph();
                updateTroungCount();

                update();
            }
        }, 20);
        // 0.02 second, sensor sample data period = 5 sec / sampleSize
    }

    private void updateGraph() {
        Map.Entry<Double, Double> lastData = this.data.getLastData();
        if (lastData == null) {
            return;
        }
        series.appendData(new DataPoint(lastData.getKey(), lastData.getValue()), false, 550);

        graph.getViewport().setMinX(lastData.getKey() - 5);
        graph.getViewport().setMaxX(lastData.getKey() + 0.01);
    }


    private void updateTroungCount() {
        if (lastXPoint > 10) {

            double end = this.data.getLastData().getKey();

            this.troungCount = data.getTroungCount(end, 0.75);
        }

        String tCount = String.format(" Troung Count: %d on %.2f ", this.troungCount, lastXPoint);
        TextView countText = findViewById(R.id.troungCount);
        countText.setText(tCount);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
//            Sensor.TYPE_LIGHT:
//            values[0]: Ambient light level in SI lux units

            long now = System.currentTimeMillis();

            lastXPoint += (now - from) / 1000.0;

//            Log.d(TAG,lastXPoint + "--" + (now - from) / 1000.0 + "--" + event.values[0]);

            this.data.putData(lastXPoint, event.values[0]);

            String dis = String.format("Light Sensor: %f lux on %.2f\n %f, %f, %f", event.values[0], lastXPoint, event.values[0], event.values[1], event.values[2]);
            TextView sensorVal = findViewById(R.id.sensorVal);
            sensorVal.setText(dis);

            this.from = now;

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumIntegerDigits(String.format("%f", event.values[0]).length());
            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

}
