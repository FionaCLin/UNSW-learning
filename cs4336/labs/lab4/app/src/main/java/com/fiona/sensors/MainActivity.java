package com.fiona.sensors;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLight;
    private Sensor mAccelerometer;

    private  int count;

    private ArrayList<String> sensorsList = new ArrayList<>();
    private List<Sensor> results ;
    private ArrayAdapter adapter;
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button show = (Button) findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchSensor();
            }
        });


        final Button pos = (Button) findViewById(R.id.position);
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PositionActivity.class));
            }
        });


        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,sensorsList);

        final ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(getApplicationContext(), AccelerometerActivity.class));
            }
        });

    }

    private void fetchSensor() {
        count = 1;
        sensorsList.clear();
        results = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(Sensor s : results) {
            String dis = String.format("%d) Name: %s\nVendors: %s\nVersion: %d\nMaximumRange: %f\nMinDelay: %d",
                    count, s.getName(),s.getVendor(), s.getVersion(),s.getMaximumRange(), s.getMinDelay());
            sensorsList.add(dis);
            adapter.notifyDataSetChanged();
            count++;
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
}