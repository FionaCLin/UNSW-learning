package com.fiona.sensor2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.hardware.GeomagneticField;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mGyroscope;

//
//    private LocationManager locationManager;
//    //    private boolean isGPSEnabled;
//    private Location loc;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;
//    private private GeomagneticField geoField;

    private static final float NS2S = 1.0f / 1000000000.0f;

    private float timestamp;

    private float angle[] = new float[3];
    private float angleD[] = new float[3];
    private float angela;
//    private GeomagneticField geoField;
//    private Sensor mMagneticSensor;


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

//        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        mSensorManager.registerListener(this, this.mGyroscope,
                SensorManager.SENSOR_DELAY_NORMAL);
//        mSensorManager.registerListener(this, this.mMagneticSensor,
//                SensorManager.SENSOR_DELAY_GAME);


//        getLoc();
//        if (loc != null) {
//            geoField = new GeomagneticField(
//                    Double.valueOf(loc.getLatitude()).floatValue(),
//                    Double.valueOf(loc.getLongitude()).floatValue(),
//                    Double.valueOf(loc.getAltitude()).floatValue(),
//                    System.currentTimeMillis()
//            );
//        }
    }

//
//    private void getLoc() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getBaseContext(), "Fine location No premission granted " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                    , Toast.LENGTH_LONG).show();
//            Toast.makeText(getBaseContext(), "Coarse location No premission granted " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    , Toast.LENGTH_LONG).show();
//
//            return;
//        } else {
//
//
////            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
////            loc = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
//
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//            if (loc != null) {
//
//                Date date = new Date(loc.getTime());
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String dis = String.format("Date/Time: %s\nProvider: %s\nAccuracy: %f\nAltitude: %f\nLatitude: %f\nSpeed: %f",
//                        sdf.format(date), loc.getProvider(), loc.getAccuracy(), loc.getAltitude(), loc.getLatitude(), loc.getSpeed());
//                Toast.makeText(getBaseContext(), dis, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        TextView display = (TextView) findViewById(R.id.display);

//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//
//            // 根据三个方向上的加速度值得到总的加速度值a
//            float a = (float) Math.sqrt(x * x + y * y + z * z);
//
//            System.out.println("a---------->" + a);
//
////                      System.out.println("magneticSensor.getMinDelay()-------->"
////                    + mMagneticSensor.getMinDelay());
//
//
//            Log.d("jarlen", "x------------->" + x);
//            Log.d("jarlen", "y------------>" + y);
//            Log.d("jarlen", "z----------->" + z);
//
//        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            //axies on magnetic field，unit micro-Tesla(AKA uT or Gauss), 1Tesla=10000Gauss
//
//
//            // 磁场感应器的最大量程
//            System.out.println("event.sensor.getMaximumRange()----------->"
//                    + event.sensor.getMaximumRange());
//
//            System.out.println("x------------->" + x);
//            System.out.println("y------------->" + y);
//            System.out.println("z------------->" + z);
//
//
//        } else
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
                    angelResult += "\nangle" + label + ": " + angle[i] + "-Degree->" + angleD[i];
                    label++;
                }

                angelResult += "\nangela-->" + a + "-Degree->" + angela + "(" + angela % 360 + ")";
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
//
//    @Override
//    public void onLocationChanged(Location location) {
//        this.loc = location;
//        final TextView status = (TextView) findViewById(R.id.display);
//        if (location != null) {
//
//            Date date = new Date(location.getTime());
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String dis = "My current location at Date/Time= " + sdf.format(date) + " is:\n";
//            dis += "Longtitud: " + location.getLongitude() + "\n";
//            dis += "Latitude: " + location.getLatitude() + "\n";
//            dis += "My Speed: " + location.getSpeed() + "\n";
//            dis += "GPS Accuracy: " + location.getAccuracy() + "\n";
//
//            Toast.makeText(getBaseContext(), dis, Toast.LENGTH_LONG).show();
//
//        }
//    }

}
