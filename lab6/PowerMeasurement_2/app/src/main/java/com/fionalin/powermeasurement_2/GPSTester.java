package com.fionalin.powermeasurement_2;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GPSTester implements LocationListener {

    LocationManager locationManager;
    private boolean isGPSEnabled;
    private Location location;
    private Context context;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;
    private String status = "";

    public GPSTester(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private void getLoc() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Fine location No premission granted " + ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    , Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Coarse location No premission granted " + ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    , Toast.LENGTH_LONG).show();

            return;
        } else {


//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, context);
//            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) context);
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (location != null) {

                Date date = new Date(location.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dis = String.format("Date/Time: %s\nProvider: %s\nAccuracy: %f\nAltitude: %f\nLatitude: %f\nSpeed: %f",
                        sdf.format(date), location.getProvider(), location.getAccuracy(), location.getAltitude(), location.getLatitude(), location.getSpeed());
                Toast.makeText(context, dis, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isGPSEnabled() {
        isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }

    public void getStatus() {
        isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            Toast.makeText(context, "GPS is not active", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder
                    .setTitle("Setting the GPS").setView(R.layout.settings)
                    .setCancelable(true)
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if context button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else {
            Toast.makeText(context, "GPS is active", Toast.LENGTH_SHORT).show();
        }

    }

    public void showLoc() {
        if (isGPSEnabled) {
            Toast.makeText(context, this.status, Toast.LENGTH_SHORT).show();
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
    public void onLocationChanged(Location location) {
        if (location != null) {
            Date date = new Date(location.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dis = "My current location at Date/Time= " + sdf.format(date) + " is:\n";
            dis += "Longtitud: " + location.getLongitude() + "\n";
            dis += "Latitude: " + location.getLatitude() + "\n";
            dis += "My Speed: " + location.getSpeed() + "\n";
            dis += "GPS Accuracy: " + location.getAccuracy() + "\n";
            status = dis;
            showLoc();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // NO-OP
    }
}
