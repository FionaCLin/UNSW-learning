package com.fionalin.lab9;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;


//    Based on the Android documents, there are five data storage options:
//    1. Shared Preferences, for store private primitive data in key-value pairs.
//    2. Internal Storage, for store private data on the device memory.
//    3. External Storage, for store public data on the shared external storage.
//    4. SQLite Databases, for store structured data in a private database.
//    5. Network Connection, for store data on the web with your own network
//    server.
//    We will learn about first three in this lab and the last two in the next lab.
//

    private EditText editText;
    private Button save;
    private SharedPreferences sharedPref;
    private TextView textView;
    private Button get;
    private Button check;
    private Button saveFile;
    private int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;


    private LocationManager locationManager;
    //    private boolean isGPSEnabled;
    private Location loc;

    // for wifi scan
    WifiManager wifi;
    List<ScanResult> results;
    HashMap<String, ScanResult> wifi_res = new HashMap<>();


    //for ListView update
    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        sharedPref = getPreferences(Context.MODE_PRIVATE);

        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();

            }
        });

        get = (Button) findViewById(R.id.retrive);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        check = (Button) findViewById(R.id.chk);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExternalStorageWritable()) {
                    StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                    long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
                    double GigsAvailable = bytesAvailable / (1024 * 1024 * 1024.0);
                    String dis = String.format("External storage available capacity: %.2f Gb", GigsAvailable);
                    textView.setText(dis);
                }
            }
        });

        saveFile = (Button) findViewById(R.id.savefile);
        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFile();
            }
        });


        String random = sharedPref.getString("random string", "");
        textView.setText(random);
    }

    private void saveToFile() {

        try {
            if (isExternalStorageWritable()) {


                // Check whether this app has write external storage permission or not.
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                // If do not grant write external storage permission.
                if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    // Request user to grant write external storage permission.
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                } else {

                    // Save email_public.txt file to /storage/emulated/0/DCIM folder
                    String publicDcimDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

                    File newFile = new File(publicDcimDirPath, "apList.txt");

                    FileWriter fw = new FileWriter(newFile);


                    scan(fw);
                    fw.flush();

                    fw.close();

                    Toast.makeText(getApplicationContext(), "Save to public external storage success. File Path " + newFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception ex) {
            Log.e("", ex.getMessage(), ex);

            Toast.makeText(getApplicationContext(), "Save to public external storage failed. Error message is " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void scan(FileWriter fw) {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
        }

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                unregisterReceiver(this);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        arrayList.clear();
        wifi.startScan();
        wifi_res.clear();
        results = wifi.getScanResults();

        int count = 0;
        int size = 0;
        try {

            for (ScanResult ap : results) {
                if (!wifi_res.keySet().contains(ap.SSID)) {
                    wifi_res.put(ap.SSID, ap);
                } else {
                    ScanResult ap_ext = wifi_res.get(ap.SSID);
                    if (ap_ext.level < ap.level) {
                        wifi_res.remove(ap_ext.SSID);
                        wifi_res.put(ap.SSID, ap);
                    }
                }
            }
            fw.write("Date\t\t\tLat\tLoc\tSSID\t\t\tSignal Strength\n");
            size = wifi_res.size();
            for (String k : wifi_res.keySet()) {
                ScanResult ap = wifi_res.get(k);
                if (!ap.SSID.isEmpty() && count < size) {
                    String dis = (++count) + " " + ap.SSID + " " + ap.BSSID + " " + ap.level;
                    dis += " " + ap.capabilities.replace("[", "").split("-")[0] + '\n';
                    arrayList.add(dis);
                    SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

                    String writeEntry = f.format(new Date(ap.timestamp));

                    getLoc();
                    if (loc != null) {
                        writeEntry += String.format(" %.2f %.2f ", loc.getLatitude(), loc.getLongitude());
                    }

                    writeEntry += String.format("%1$-30s %2$10d\n", ap.SSID, ap.level);

                    fw.write(writeEntry);
                }
            }
        } catch (Exception e) {
        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void show() {
        String random = sharedPref.getString("random string", "");
        textView.setText(random);
    }

    private void save() {
        String msg = editText.getText().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("random string", msg);
        editor.commit();
    }

    public File getPublicDocStorageDir() {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), null);
        if (!file.mkdirs()) {
            Log.e("", "Directory not created");
        }
        return file;
    }

    private void getLoc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getBaseContext(), "Fine location No premission granted " + ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    , Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "Coarse location No premission granted " + ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    , Toast.LENGTH_LONG).show();

            return;
        } else {


//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//            loc = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, MainActivity.this);
            loc = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            if (loc != null) {

                Date date = new Date(loc.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dis = String.format("Date/Time: %s\nProvider: %s\nAccuracy: %f\nAltitude: %f\nLatitude: %f\nSpeed: %f",
                        sdf.format(date), loc.getProvider(), loc.getAccuracy(), loc.getAltitude(), loc.getLatitude(), loc.getSpeed());
//                Toast.makeText(getBaseContext(), dis, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.loc = location;
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
}
