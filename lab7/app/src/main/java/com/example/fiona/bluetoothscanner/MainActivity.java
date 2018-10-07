package com.example.fiona.bluetoothscanner;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    BluetoothAdapter btAdapter;

    //for ListView update
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter mArrayAdapter;
    private BroadcastReceiver mReceiver;
    private IntentFilter filter;


    private static final UUID MY_UUID = UUID.randomUUID();

    private Button send = null;
    private EditText editMsg = null;
    private ArrayList<String> devices = new ArrayList<>();
    private ConnectingThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button check = (Button) findViewById(R.id.check);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBT();

            }
        });

        final Button scan = (Button) findViewById(R.id.discover);
        scan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                discover(); /* my method to call new intent or activity */
            }
        });

//        this.send = (Button) findViewById(R.id.send);
//        this.editMsg = (EditText) findViewById(R.id.editText);

        editMsg.setInputType(EditorInfo.TYPE_NULL);
        send.setEnabled(false);
//        Toast.makeText(this, editMsg.getInputType(), Toast.LENGTH_SHORT).show();
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String msg = editMsg.getText().toString();

                sendMessage(msg); /* my method to call new intent or activity */
            }
        });

        // listview update listener
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        final ListView list = findViewById(R.id.list);
        list.setAdapter(mArrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Start an alpha animation for clicked item
                String device_name = ((TextView) view).getText().toString().split("\n")[0];
                String macaddr = ((TextView) view).getText().toString().split("\n")[1];
                connect(device_name, macaddr);

            }
        });


        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                TextView status = (TextView) findViewById(R.id.btStatus);
                String toastString = "";
                String dis = "";
                checkBTPermissions();

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    String device_name = device.getName();
                    String macaddr = device.getAddress();
                    devices.add(device_name + "\n" + macaddr);
                    toastString = "Bluetooth device found: " + device_name;
                } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {

                    toastString = "Bluetooth discovery started";
                } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    dis = "SEARCHING. FOUND: " + devices.size();

                    addToList();
                    toastString = "Bluetooth discovery finished";
                }
                status.setText(dis);
                Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
            }
        };
        // Create a BroadcastReceiver for ACTION_FOUND
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // Register the BroadcastReceiver
        registerReceiver(mReceiver, filter);

    }

    private void addToList() {
        for (String device : this.devices) {
            mArrayAdapter.add(device);
        }
    }

    private void connect(String device_name, String macaddr) {

        Toast.makeText(this, device_name + "\n" + macaddr, Toast.LENGTH_SHORT).show();

        send.setEnabled(true);
        editMsg.setEnabled(true);

        BluetoothDevice pairDevice = btAdapter.getRemoteDevice(macaddr);
        pairDevice.createBond();
        thread = new ConnectingThread(macaddr, MY_UUID, btAdapter);
    }

    private void sendMessage(String msg) {

        Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
        // Initiate a connection request in a separate thread
        // need to exchange the UUID and establish the socket connection with the same uuid
//        thread.run();
    }

    private void discover() {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: canceling discovery");
        }
        mArrayAdapter.clear();
        boolean res = btAdapter.startDiscovery();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice btd : pairedDevices) {
            try {
                Method mth = btd.getClass().getMethod("removeBond", (Class[]) null);
                mth.invoke(btd, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        String dis = "res->" + res + "Total paired devices: " + pairedDevices.size();


        registerReceiver(mReceiver, filter);

    }

    private void enableDisableBT() {
        TextView status = (TextView) findViewById(R.id.btStatus);
        if (btAdapter == null) {
            Log.d(TAG, "Your phone does not support Bluetooth");
        } else if (btAdapter.isEnabled()) {
            status.setText("BLUETOOTH IS ENABLED.");

        } else {
            status.setText("BLUETOOTH IS DISABLED. ENABLING");
            btAdapter.enable();
        }
    }

    private void checkBTPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getBaseContext(), "Fine location No premission granted " + ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                    , Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "Coarse location No premission granted " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    , Toast.LENGTH_LONG).show();

            return;
        } else {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
                if (permissionCheck != 0) {

                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
                }
            } else {
                Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
            }
        }
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


}
