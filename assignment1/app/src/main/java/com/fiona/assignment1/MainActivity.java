package com.fiona.assignment1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Handler mHandler = new Handler();
    private long lastXPoint = 0;

    // Sensor
    private SensorManager mSensorManager;
    private Sensor mLight;


    private LineGraphSeries<DataPoint> series;
    private GraphView graph;
    private Viewport viewport;

    private LineGraphSeries<DataPoint> series2;
    private GraphView graph2;
    private Viewport viewport2;
    private Random rnd = new Random();
    private float val = 0;
    private int troungCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[]{
        });

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


        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        // TODO UPDATE the frequence as assignment spec required
        mSensorManager.registerListener(this, mLight, mSensorManager.SENSOR_DELAY_NORMAL);

        graph2 = (GraphView) findViewById(R.id.graph2);
        series2 = new LineGraphSeries<>(new DataPoint[]{
        });

        graph2.addSeries(series2);
        viewport2 = graph2.getViewport();

        viewport2.setXAxisBoundsManual(true);
        viewport2.setMinX(lastXPoint - 5);
        viewport2.setMaxX(lastXPoint + 5);

        viewport2.setYAxisBoundsManual(true);
        viewport2.setMinY(-5);
        viewport2.setMaxY(5);
        viewport2.setScrollable(true);
        updateGraph();

    }

    private void updateGraph() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint++;

                System.out.println(lastXPoint + "--" + val);

                series.appendData(new DataPoint(lastXPoint, val), false, 1000);
                viewport.setMinX(lastXPoint - 5);
                viewport.setMaxX(lastXPoint + 1);


                series2.appendData(new DataPoint(lastXPoint, rnd.nextInt(10) - 5), false, 1000);
                viewport2.setMinX(lastXPoint - 5);
                viewport2.setMaxX(lastXPoint + 1);
                updateGraph();
            }
        }, 1000);
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


            String tCount = "Troung Count: " + this.troungCount + " on " + lastXPoint;
            TextView troungCount = findViewById(R.id.troungCount);
            troungCount.setText(tCount);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
