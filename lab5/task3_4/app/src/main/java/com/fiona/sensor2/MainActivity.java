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

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private SensorManager mSensorManager;

    private LocationManager locationManager;
    //    private boolean isGPSEnabled;
    private Location loc;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;


    private float angle[] = new float[3];
    private float angleD[] = new float[3];
    private float angela;

    private GeomagneticField geoField;
    private Sensor mMagneticSensor;


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

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, this.mMagneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        getLoc();
        if (loc != null) {
            geoField = new GeomagneticField(
                    Double.valueOf(loc.getLatitude()).floatValue(),
                    Double.valueOf(loc.getLongitude()).floatValue(),
                    Double.valueOf(loc.getAltitude()).floatValue(),
                    System.currentTimeMillis()
            );
        }
    }


    private void getLoc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getBaseContext(), "Fine location No premission granted " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    , Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "Coarse location No premission granted " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    , Toast.LENGTH_LONG).show();

            return;
        } else {


//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//            loc = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (loc != null) {

                Date date = new Date(loc.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dis = String.format("Date/Time: %s\nProvider: %s\nAccuracy: %f\nAltitude: %f\nLatitude: %f\nSpeed: %f",
                        sdf.format(date), loc.getProvider(), loc.getAccuracy(), loc.getAltitude(), loc.getLatitude(), loc.getSpeed());
                Toast.makeText(getBaseContext(), dis, Toast.LENGTH_LONG).show();
            }
        }
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
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //axies on magnetic field，unit micro-Tesla(AKA uT or Gauss), 1Tesla=10000Gauss


            // 磁场感应器的最大量程
            System.out.println("event.sensor.getMaximumRange()----------->"
                    + event.sensor.getMaximumRange());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, this.mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.loc = location;
        final TextView status = (TextView) findViewById(R.id.display);
        if (location != null) {

            Date date = new Date(location.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dis = "My current location at Date/Time= " + sdf.format(date) + " is:\n";
            dis += "Longtitud: " + location.getLongitude() + "\n";
            dis += "Latitude: " + location.getLatitude() + "\n";
            dis += "My Speed: " + location.getSpeed() + "\n";
            dis += "GPS Accuracy: " + location.getAccuracy() + "\n";

            Toast.makeText(getBaseContext(), dis, Toast.LENGTH_LONG).show();

        }
    }

}
