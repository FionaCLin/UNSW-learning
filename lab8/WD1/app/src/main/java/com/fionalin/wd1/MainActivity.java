package com.fionalin.wd1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button discover;
    Button check;
    TextView status;
    WifiP2pConnector connector;
    private WifiP2pManager WD_Manager;
    private ArrayAdapter<String> mArrayAdapter;
    private String addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connector = new WifiP2pConnector(this);
        WD_Manager = connector.getmManager();

        status = (TextView) findViewById(R.id.status);
        check = (Button) findViewById(R.id.check);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (WD_Manager.WIFI_P2P_STATE_ENABLED != 2) {
                    //there is no WifiDirect interface
                    status.setText("Wifi-Direct is not available");
                } else {
                    discover.setEnabled(true);
                    status.setText("Wifi-Direct is available");
                }

            }
        });

        discover = (Button) findViewById(R.id.discover);

        discover.setEnabled(false);
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connector.discoverPeers();
            }
        });

        // listview update listener
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        final ListView list = findViewById(R.id.list);
        list.setAdapter(mArrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Start an alpha animation for clicked item
                String device_name = ((TextView) view).getText().toString().split("\n")[0].split(": ")[1];
                addr = ((TextView) view).getText().toString().split("\n")[1].split(": ")[1];
                String dis = "Connect with "+device_name+" in 26 secs";
//// Is that ok A confirmation dialog showing?
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//                alertDialogBuilder
//                        .setTitle("Invitation to connect").setView(R.layout.invitation)
//                        .setMessage(dis)
//                        .setCancelable(true)
//                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
                               connector.connectToPeer(addr);
//                            }
//                        })
//                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // if context button is clicked, just close
//                                // the dialog box and do nothing
//                                dialog.cancel();
//                            }
//                        });
//                // create alert dialog
//                AlertDialog alertDialog = alertDialogBuilder.create();
//
//                // show it
//                alertDialog.show();

            }
        });


    }

    public void displayPeers() {
        mArrayAdapter.clear();
        for (WifiP2pDevice device : connector.getPeerList()) {
            mArrayAdapter.add(device.toString());
        }

    }

}
