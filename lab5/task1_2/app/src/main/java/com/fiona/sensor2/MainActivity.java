package com.fiona.sensor2;

import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mGyroscope;


    private static final float NS2S = 1.0f / 1000000000.0f;

    private float timestamp;

    private float angle[] = new float[3];
    private float angleD[] = new float[3];
    private float angela;


    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 3; i++) {
                    angleD[i] = 0;
                    angle[i] = 0;
                }
                TextView display = (TextView) findViewById(R.id.rotation);
                String dis = "Rotation: \n 0";
                display.setText(dis);
            }
        });


        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView display = (TextView) findViewById(R.id.rotation);
                String dis = "Rotation: \n" + angela % 360;
                display.setText(dis);
            }
        });

        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        mSensorManager.registerListener(this, this.mGyroscope,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        TextView display = (TextView) findViewById(R.id.display);

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值

            if (timestamp != 0) {
                // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒

                final float dT = (event.timestamp - timestamp) * NS2S;


                float a = 0;
                // 将弧度转化为角度
                angleD = new float[3];

                // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                for (int i = 0; i < 3; i++) {
                    angle[i] += event.values[i] * dT;
                    angleD[i] = (float) Math.toDegrees(angle[i]);
                    a += (angle[i] * angle[i]);
                }

                // 根据三个方向上的加速度值得到总的加速度值a
                a = (float) Math.sqrt(a);
                System.out.println("a---------->" + a);
                angela = (float) Math.toDegrees(a);

                String angelResult = "Sensor value  on this phones: ";

                char label = 'x';
                for (int i = 0; i < 3; i++) {
                    angelResult += "\nangle" + label + ": " + angle[i] + "\n-Degree->" + angleD[i];
                    label++;
                }

                angelResult += "\nangela-->" + a + "\n-Degree->" + angela + "(" + angela % 360 + ")";
                display.setText(angelResult);
            }


            //将当前时间赋值给timestamp
            timestamp = event.timestamp;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, this.mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}
