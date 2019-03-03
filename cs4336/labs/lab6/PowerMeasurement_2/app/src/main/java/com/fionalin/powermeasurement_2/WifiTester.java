package com.fionalin.powermeasurement_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiTester {

    // for wifi scan
    WifiManager wifi;
    List<ScanResult> results;
    HashMap<String, ScanResult> wifi_res = new HashMap<>();

    // for wifi connect
    WifiConfiguration config = new WifiConfiguration();
    WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
    int highest = 0;


    public WifiTester(final Context context) {

        this.wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        this.config = new WifiConfiguration();
        this.enterpriseConfig = new WifiEnterpriseConfig();
        this.highest = 0;

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                context.unregisterReceiver(this);

            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public boolean isEnable() {
        return wifi.isWifiEnabled();
    }

    public boolean enable() {
        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
        }
        return wifi.isWifiEnabled();
    }


    public void scanWifi() {
        wifi_res.clear();
        results = wifi.getScanResults();

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


        } catch (Exception e) {
        }
    }

}
