package com.fionalin.lab10;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanWifiActivity extends AppCompatActivity {


    int size = 0;

    // for wifi scan
    WifiManager wifi;
    List<ScanResult> results;
    HashMap<String, ScanResult> wifi_res = new HashMap<>();

    // for wifi connect
    WifiConfiguration config = new WifiConfiguration();
    WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
    int highest = 0;

    //for ListView update
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi);


        final Button scan = (Button) findViewById(R.id.scan_button);
        scan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myClick(); /* my method to call new intent or activity */
            }
        });


        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
        }
        // Get WiFi status
        TextView txCounter = (TextView) findViewById(R.id.result_title);

        // listview update listener
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        final ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Start an alpha animation for clicked item
                String SSID = ((TextView) view).getText().toString().split(" ")[1];

                ScanResult ap = wifi_res.get(SSID);
                inputCredential(ap);
            }
        });


        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                size = results.size();
                unregisterReceiver(this);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    public void inputCredential(final ScanResult accesspoint) {
        // get prompts.xml view
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View promptsView = li.inflate(R.layout.connect, null, false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Enter username:password");


        final ScanResult ap = accesspoint;
        final EditText username = (EditText) promptsView
                .findViewById(R.id.username);

        final EditText password = (EditText) promptsView
                .findViewById(R.id.password);

        // set dialog message
        alertDialogBuilder
                .setView(promptsView)
                .setMessage("Click yes to connect!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        connectToAp(accesspoint, username.getText().toString(), password.getText().toString());

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void connectToAp(ScanResult accesspoint, String user, String pwd) {

        config.SSID = "\"" + accesspoint.SSID + "\"";
        config.preSharedKey = "\"" + pwd + "\"";
//                        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//                        config.status = WifiConfiguration.Status.ENABLED;
//                        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
//                        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
//                        enterpriseConfig.setIdentity(username.getText().toString());
//                        enterpriseConfig.setPassword(password.getText().toString());
//                        enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
//                        config.enterpriseConfig = enterpriseConfig;

        int netId = wifi.addNetwork(config);
        wifi.disconnect();
        wifi.enableNetwork(netId, true);
        wifi.reconnect();
        wifi.saveConfiguration();
    }


    public void myClick() {
        arrayList.clear();
        wifi.startScan();
        wifi_res.clear();
        results = wifi.getScanResults();

        int count = 0;
        int size = 5;
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

            for (String k : wifi_res.keySet()) {
                ScanResult ap = wifi_res.get(k);
                if (!ap.SSID.isEmpty() && count < size) {
                    String dis = (++count) + " " + ap.SSID + " " + ap.BSSID + " " + ap.level;
                    dis += " " + ap.capabilities.replace("[", "").split("-")[0];
                    arrayList.add(dis);
                    adapter.notifyDataSetChanged();
                }
            }

        } catch (Exception e) {
        }
    }

}