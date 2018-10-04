package com.example.fiona.bluetoothscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class ConnectingThread extends Thread {
    private static final String TAG = "ConnectThread";

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private BluetoothAdapter btAdapter;

    public ConnectingThread(String macaddr, UUID uuid, BluetoothAdapter btAdapter) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.

        BluetoothSocket tmp = null;
        mmDevice = btAdapter.getRemoteDevice(macaddr);
        this.btAdapter = btAdapter;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = mmSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        btAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);
    }
//    // Call this from the main activity to send data to the remote device.
//    public void write(byte[] bytes) {
//        try {
//            mmOutStream.write(bytes);
//
//            // Share the sent message with the UI activity.
//            Message writtenMsg = mHandler.obtainMessage(
//                    MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
//            writtenMsg.sendToTarget();
//        } catch (IOException e) {
//            Log.e(TAG, "Error occurred when sending data", e);
//
//            // Send a failure message back to the activity.
//            Message writeErrorMsg =
//                    mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//            Bundle bundle = new Bundle();
//            bundle.putString("toast",
//                    "Couldn't send data to the other device");
//            writeErrorMsg.setData(bundle);
//            mHandler.sendMessage(writeErrorMsg);
//        }
//    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}