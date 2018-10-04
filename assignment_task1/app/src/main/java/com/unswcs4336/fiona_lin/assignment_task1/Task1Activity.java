package com.unswcs4336.fiona_lin.assignment_task1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;

public class Task1Activity extends AppCompatActivity implements SensorEventListener {

    static final int MIN = 0, MAX = 1, SUM = 2, MEAN = 3, SD = 4;
    static private int index = 0;
    static private int sampleInterval = 0;


    private long from = System.currentTimeMillis();
    private double lastXPoint = 0;

    // Sensor
    private SensorManager mSensorManager;
    private Sensor mLight;


    private LineGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> seriesS;
    private GraphView graph;

    private int sampleDuration = 5000000; // microsec
    private int sampleSize = 5000; // num of sample
    private int samplePeroid = sampleDuration / sampleSize; // microsec
    private GridLabelRenderer glr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task1);

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[]{});

        initGraphView(series, graph);


        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mSensorManager.registerListener(this, mLight, samplePeroid);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
//            Sensor.TYPE_LIGHT:
//            values[0]: Ambient light level in SI lux units

            long now = System.currentTimeMillis();
            lastXPoint += (now - from) / 1000.0;

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumIntegerDigits(event.values.toString().length());

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
            series.appendData(new DataPoint(lastXPoint, event.values[0]), false, 550);

            graph.getViewport().setMinX(lastXPoint - 5);
            graph.getViewport().setMaxX(lastXPoint + 0.01);


            String dis = String.format("Light Sensor: %f lux on %.2f", event.values[0], lastXPoint);
            TextView sensorVal = findViewById(R.id.sensorVal);
            sensorVal.setText(dis);


            this.from = now;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
