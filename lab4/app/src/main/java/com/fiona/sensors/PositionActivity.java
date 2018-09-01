package com.fiona.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PositionActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private String position;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);


        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        // Do something with this sensor value.
//        Toast.makeText(getBaseContext(), dis, Toast.LENGTH_LONG).show();
//        final TextView textView = (TextView) findViewById(R.id.sensor);
//        textView.setText(dis);

        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        final TextView pos = (TextView) findViewById(R.id.pposition);
        String position = this.position;
        if (event.values[0] > 8 && Math.abs(event.values[1]) < 1 && Math.abs(event.values[2]) < 1) {
            position = "Left";
        } else if (event.values[0] > -8 && Math.abs(event.values[1]) < 1 && Math.abs(event.values[2]) < 1) {
            position = "Right";
        } else if (Math.abs(event.values[0]) < 1 && event.values[1] > 8 && Math.abs(event.values[2]) < 1) {
            position = "Default";
        } else if (Math.abs(event.values[0]) < 1 && event.values[1] > -8 && Math.abs(event.values[2]) < 1) {
            position = "Upside down";
        } else if (Math.abs(event.values[0]) < 1 && Math.abs(event.values[1]) < 1 && event.values[2] > 8) {
            position = "On the table";
        }
        if (this.position != position) {
            pos.setText(position);
            this.position = position;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}
