package com.fionalin.wd1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Collection;

public class WifiP2pConnector extends BroadcastReceiver {

    private final IntentFilter intentFilter = new IntentFilter();

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pDeviceList peerList;

    public WifiP2pConnector(Activity context) {
        setupIntentFilter();
        context.registerReceiver(this, intentFilter);
        this.setupWifiP2p(context);

    }

    public Collection<WifiP2pDevice> getPeerList() {
        return peerList.getDeviceList();
    }

    public WifiP2pManager getmManager() {
        return mManager;
    }

    public WifiP2pDevice getPeer(String address) {
        return peerList.get(address);
    }

    private void setupWifiP2p(Context context) {
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, context.getMainLooper(), null);
    }


    public void stopReceiver(Activity context) {
        context.unregisterReceiver(this);

    }

    public void setupIntentFilter() {
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(final Context context, Intent intent) {

        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            } else {

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed!  We should probably do something about
            // that.

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        if (peers != null) {
                            peerList = peers;
                            Method mth = null;
                            try {
                                mth = context.getClass().getMethod("displayPeers", (Class[]) null);
                                mth.invoke(context, (Object[]) null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {

                        // InetAddress from WifiP2pInfo struct.
                        InetAddress groupOwnerAddress = info.groupOwnerAddress;

                        // After the group negotiation, we can determine the group owner.
                        if (info.groupFormed && info.isGroupOwner) {
                            // Do whatever tasks are specific to the group owner.
                            // One common case is creating a server thread and accepting
                            // incoming connections.
                        } else if (info.groupFormed) {
                            // The other device acts as the client. In this case,
                            // you'll want to create a client thread that connects to the group
                            // owner.

//                            Neighbor neighbor = new Neighbor(groupOwnerAddress.getHostAddress(),groupOwnerAddress.getHostName(),Neighbor.TYPE_WIFI_P2P);
//                            mNearbyListener.foundNeighbor(neighbor);
                        }
                    }
                });
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {


        }

    }

    public void discoverPeers() {
        this.disconnect();
        mManager.discoverPeers(mChannel, null);
    }

    public void connectToPeer(String addr) {
        final WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = addr;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Log.d("", "Connected");


            }

            @Override
            public void onFailure(int reason) {

            }
        });

    }

    public void disconnect() {
        if (mManager != null && mChannel != null) {
            mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && mManager != null && mChannel != null
                            && group.isGroupOwner()) {
                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d("", "removeGroup onSuccess -");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d("", "removeGroup onFailure -" + reason);
                            }
                        });
                    }
                }
            });
        }
    }

}