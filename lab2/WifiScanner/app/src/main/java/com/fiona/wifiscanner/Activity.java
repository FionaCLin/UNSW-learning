package com.fiona.wifiscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity extends AppCompatActivity {

    int size = 0;

    // for wifi scan
    WifiManager wifi;
    List<ScanResult> results;

    //for ListView update
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);

        final Button scan = (Button) findViewById(R.id.scan_button);
        scan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myClick(); /* my method to call new intent or activity */
            }
        });


        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifi.isWifiEnabled()){
            wifi.setWifiEnabled(true);
        }
        // Get WiFi status
        TextView txCounter = (TextView) findViewById(R.id.result_title);

        // listview update listener
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrayList);

        final ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

//        WifiConfiguration config= new WifiConfiguration();
//        config.SSID=”\”SSID_OF_NETOWRK\””;
//        config.allowedKeyManagement.set(KeyMgmt.NONE);
//        config.status=WifiConfiguration.Status.ENABLED;
//        int netId=mWifiManager.addNetwork(config);
//        mWifiManager.saveConfiguration();
//        mWifiManager.reconnect();
    }

    public void myClick() {
        arrayList.clear();
        wifi.startScan();
        results = wifi.getScanResults();
        HashMap<String, ScanResult> wifi_res = new HashMap<>();
        int count = 0;
        int size = 5;
        try
        {


            for(ScanResult ap : results) {
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

            for(String k : wifi_res.keySet()) {
                ScanResult ap = wifi_res.get(k);
                if(!ap.SSID.isEmpty() && count < size) {
                    String dis = (++count) + " " + ap.SSID + " " + ap.BSSID + " " + ap.level;
                    dis += " " + ap.capabilities.replace("[","").split("-")[0];
                    arrayList.add(dis);
                    adapter.notifyDataSetChanged();
                }
            }

        }  catch (Exception e) {
        }
    }


}
