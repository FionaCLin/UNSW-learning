package com.fionalin.lab10;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int size = 0;

    // for wifi scan
    WifiManager wifi;
    List<ScanResult> results;
    HashMap<String, ScanResult> wifi_res = new HashMap<>();

    // for wifi connect
    WifiConfiguration config = new WifiConfiguration();
    WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
    int highest = 0;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get WiFi 5 GHz support status
        result = (TextView) findViewById(R.id.result);


        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        final Button check = (Button) findViewById(R.id.chk);
        check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (wifi.is5GHzBandSupported()) {
                    result.setText("5GHZ is available");
                } else {
                    result.setText("5GHZ is available");
                }
            }
        });


        final Button info = (Button) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayInfo();
            }
        });
        final Button scan = (Button) findViewById(R.id.scan);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanWifiActivity.class));
            }
        });

    }

    private void displayInfo() {
/*

the protocol from the link speed

WifiManager.getConnectionInfo().getLinkSpeed()

By Wikipedia 802.11 protocols speed table you can tell if it is 802.11b, 802.11n or 802.11ag.
802.11n and 802.11ac full link speed tables
* */
//Check the frequency and bit rate of current connection and show it in GHz on the screen.
        // is this only change frequnecy?
        WifiInfo connect = wifi.getConnectionInfo();
        double frequence = connect.getFrequency() / 1000.0;
        String dis = connect.toString().replaceFirst("Frequency.*MHz", "Frequency: " + frequence + "GHz");
        dis = dis.replace(",", "\n");
        result.setText(dis);
    }
}
