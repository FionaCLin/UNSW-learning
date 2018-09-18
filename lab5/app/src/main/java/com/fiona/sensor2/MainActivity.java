package com.fiona.sensor2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private SensorManager mSensorManager;
    private Sensor mLight;
    private Sensor mGyroscope;

    private  int count;


    private LocationManager locationManager;
    //    private boolean isGPSEnabled;
    private Location loc;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;
    private private GeomagneticField geoField;


    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { }
        });


        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);


        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        getLoc();
        if( loc != null ){
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
            Toast.makeText(getBaseContext(), "Fine location No premission granted "+ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    , Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "Coarse location No premission granted "+ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    , Toast.LENGTH_LONG).show();

            return;
        } else {


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
            loc = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
//            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (loc != null){

                Date date = new Date(loc.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dis = String.format("Date/Time: %s\nProvider: %s\nAccuracy: %f\nAltitude: %f\nLatitude: %f\nSpeed: %f",
                        sdf.format(date),loc.getProvider() , loc.getAccuracy() , loc.getAltitude(), loc.getLatitude(),loc.getSpeed());
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
        float lux = event.values[0];
        // Do something with this sensor value.
        String dis = String.format("Light Sensor x: %.2f, y: %.2f, z: %.2f", event.values[0], event.values[1], event.values[2]);
//        Toast.makeText(getBaseContext(), dis, Toast.LENGTH_LONG).show();
//        final TextView textView = (TextView) findViewById(R.id.sensor);
//        textView.setText(dis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.loc = location;
        final TextView status = (TextView) findViewById(R.id.status);
        if (location != null){

            Date date = new Date(location.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dis = "My current location at Date/Time= "+ sdf.format(date)+" is:\n";
            dis += "Longtitud: " + location.getLongitude() + "\n";
            dis += "Latitude: " + location.getLatitude()+"\n";
            dis += "My Speed: "+location.getSpeed() + "\n";
            dis += "GPS Accuracy: " + location.getAccuracy() + "\n";

            Toast.makeText(getBaseContext(), dis, Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLight;
    private Sensor mGyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start = (Button)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });

        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void start() {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
