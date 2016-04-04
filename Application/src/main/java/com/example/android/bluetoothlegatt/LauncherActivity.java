package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LauncherActivity extends Activity {

    //Dev
    boolean testing = true;
    //UI handles
    TextView device_status_tv;
    Button scan_btn;
    Button connect_btn;

    //Bluetooth members
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice target_device = null;
    private Handler scanHandler;
    private boolean scanning = false;
    private boolean target_found_in_current_scan = false;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 5000;

    private static final String TARGET_DEVICE_NAME = "BTLE_G4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanHandler = new Handler();
        setContentView(R.layout.activity_launcher);

        //UI bindings
        device_status_tv = (TextView) findViewById(R.id.ble_status_value);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        connect_btn = (Button) findViewById(R.id.connect_btn);

        //Bluetooth checks

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    public void scanClicked(View v){
        Log.w("clicked", "scan");
        scan_btn.setText(R.string.btn_scanning);
        scan_btn.setEnabled(false);
        target_found_in_current_scan = false;
        scanLeDevice(true);
    }

    public void connectClicked(View v){
        if (testing){
            final Intent intent = new Intent(this, ConnectedDevice.class);
            intent.putExtra("device_name", "test name");
            intent.putExtra("device_address", "12:43:55:78:AA:09");

            if (scanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                scan_btn.setText(R.string.btn_scan);
                scan_btn.setEnabled(true);
                scanning = false;
            }

            startActivity(intent);
        } else {
            if (target_device == null) return;
            final Intent intent = new Intent(this, ConnectedDevice.class);
            intent.putExtra("device_name", target_device.getName());
            intent.putExtra("device_address", target_device.getAddress());

            if (scanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                scan_btn.setText(R.string.btn_scan);
                scan_btn.setEnabled(true);
                scanning = false;
            }

            startActivity(intent);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            scanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    scan_btn.setText(R.string.btn_scan);
                    scan_btn.setEnabled(true);
                    invalidateOptionsMenu();
                    if (!target_found_in_current_scan) setTargetUnavailable();
                }
            }, SCAN_PERIOD);

            scanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            scanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceFound(device);
                        }
                    });
                }
            };

    private void deviceFound(BluetoothDevice device){
        String device_name = device.getName();
        if (device_name != null){
            if (device_name.equals(TARGET_DEVICE_NAME)){
                targetDeviceFound(device);
            }
        }
    }

    private void targetDeviceFound(BluetoothDevice device){
        target_found_in_current_scan = true;
        target_device = device;
        device_status_tv.setText(R.string.ble_available);
        device_status_tv.setTextColor(getResources().getColor(R.color.available_green));
        connect_btn.setEnabled(true);
    }

    private void setTargetUnavailable(){
        target_device = null;
        device_status_tv.setText(R.string.ble_unavailable);
        device_status_tv.setTextColor(getResources().getColor(R.color.unavailable_red));
        connect_btn.setEnabled(false);
    }

}
