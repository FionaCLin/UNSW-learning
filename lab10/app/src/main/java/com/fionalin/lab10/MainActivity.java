package com.fionalin.lab10;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
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
                if (wifi.is5GHzBandSupported()){
                    result.setText("5GHZ is available");
                } else {
                    result.setText("5GHZ is available");
                }
            }
        });



    }
}
