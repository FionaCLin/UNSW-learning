package com.fionalin.powermeasurement_2;

import android.Manifest;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();

    Intent batteryStatus;
    IntentFilter ifilter;
    private double initLevel;
    private double finalLevel;
    private double consume;
    private String state;
    private int mins = 10;
    private boolean[] tests = {true, true, true};
    private int test = -1;
    private String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    private void start() {
        GPSTester gpsTester = new GPSTester(this);
        WifiTester wifiTester = new WifiTester(this);
        if (!gpsTester.isGPSEnabled() && !wifiTester.isEnable() && tests[0]) {
            state = "Normal usage of mobile phone";
            test = 0;
            testUsage(null);
        } else if (wifiTester.isEnable() && !gpsTester.isGPSEnabled() && tests[1]) {
            state = "Using Wi-Fi";
            test = 1;
            testUsage(wifiTester);
        } else if (wifiTester.isEnable() && gpsTester.isGPSEnabled() && tests[2]) {
            state = "Using GPS";
            test = 2;
            testUsage(gpsTester);
        } else {
            int tryTest = 0;
            while (tryTest < 3) {
                if (tests[tryTest]) {
                    if (tryTest == 1) {
                        wifiTester.enable();
                        break;
                    } else if (tryTest == 2) {
                        gpsTester.getStatus();
                    }
                }
                tryTest++;
            }
        }
    }

    public void testUsage(final Object tester) {
        TextView status = (TextView) findViewById(R.id.status);

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);
        initLevel = calBatteryLevel();
        String dis = String.format("%s for %d minutes:\n\nInitial level of battery: %f %%", state, mins, initLevel);
        status.setText(dis);
        final long now = System.currentTimeMillis();

        handler.postDelayed(getUsage, mins * 60 * 1000);

    }

    Runnable getUsage = new Runnable() {
        @Override
        public void run() {
            TextView status = (TextView) findViewById(R.id.status);
            finalLevel = calBatteryLevel();
            consume = finalLevel - initLevel;
            String dis = String.format("%s for %d minutes:\n\nInitial level of battery: %f %%\n\nFinal level: %f %%\n\nConsumed battery: %f%%", state, mins, initLevel, finalLevel, consume);
            result += ('\n' + dis);
            status.setText(result);
            tests[test] = false;
        }
    };

    private double calBatteryLevel() {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (level / (float) scale) * 100;
    }

}
