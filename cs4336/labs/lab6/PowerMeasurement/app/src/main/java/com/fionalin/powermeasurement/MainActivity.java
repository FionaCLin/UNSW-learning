package com.fionalin.powermeasurement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button check = (Button) findViewById(R.id.check);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkBattery();

            }
        });
    }

    private void checkBattery() {
        //QUESTION why the app can't update the change unless restart the app??
        // so I make it this way, is that ok?

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        TextView statusDis = (TextView) findViewById(R.id.status);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = (level / (float) scale)*100;

        String dis = String.format("Current level of battery is: %.2f %%", batteryPct);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status != -1) {
            String type = getStatus(status, batteryStatus);
            if(!type.equals("")){
                dis += String.format("\nMobile is charging via %s",type);
            }
        }

        statusDis.setText(dis);


    }

    @NonNull
    private String getStatus(int status,Intent batteryStatus) {
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How we are charging
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        String type;
        switch (chargePlug) {
            case BatteryManager.BATTERY_PLUGGED_USB:
                type = "USB";
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                type = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                type = "wireless";
                break;
            default:
                type = "";
        }
        return type;
    }
}
