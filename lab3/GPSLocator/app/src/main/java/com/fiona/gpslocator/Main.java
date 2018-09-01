package com.fiona.gpslocator;

import android.Manifest;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    private boolean isGPSEnabled;
    private Location location;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button fetchStatus = (Button) findViewById(R.id.getStatus);

        final Button fetchLoc = (Button) findViewById(R.id.getLoc);

        final TextView status = (TextView) findViewById(R.id.status);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//        Toast.makeText(getBaseContext(), , Toast.LENGTH_LONG).show();

        fetchStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStatus(status);
            }
        });

        fetchLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLoc(status);
            }
        });


    }


    private void getLoc(TextView status) {
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
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
//            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (location != null){

                Date date = new Date(location.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dis = String.format("Date/Time: %s\nProvider: %s\nAccuracy: %f\nAltitude: %f\nLatitude: %f\nSpeed: %f",
                        sdf.format(date),location.getProvider() , location.getAccuracy() , location.getAltitude(), location.getLatitude(),location.getSpeed());
                status.setText(dis);
            }
        }
    }

    private void getStatus(TextView status) {
        isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            status.setText("GPS is not active");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle("Setting the GPS").setView(R.layout.settings)
                    .setCancelable(true)
                    .setPositiveButton("Settings",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else {
            status.setText("GPS is active");
        }

    }
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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
    public void onProviderDisabled(String provider) {
        // NO-OP
    }

    @Override
    public void onProviderEnabled(String provider) {
        // NO-OP
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // NO-OP
    }
}
