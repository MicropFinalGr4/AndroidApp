package com.example.android.activities;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.R;
import com.example.android.misc.Globals;
import com.example.android.managers.BluetoothLeService;
import com.example.android.managers.BluetoothServiceManager;
import com.example.android.misc.Utils;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Francis on 4/2/2016.
 */
public class ConnectedDeviceActivity extends Activity {

    //BLUETOOTH VARS
    private String device_name, device_address;
    private BluetoothLeService bluetoothLeService;
    private boolean connected = false;
    private BluetoothGattCharacteristic debug_characteristic;
    private BluetoothServiceManager bluetoothServiceManager = new BluetoothServiceManager(Globals.READ_VALUES_SERVICE_UUID);


    //CONTROL VARS
    private boolean pollerEnabled = false;

    //UI handles
        //DEBUG
        TextView debug_1, debug_2, debug_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_device);

        //UI BINDINGS
        debug_1 = (TextView) findViewById(R.id.debug_text_1);
        debug_2 = (TextView) findViewById(R.id.debug_text_2);
        debug_3 = (TextView) findViewById(R.id.debug_text_3);

        //BLUETOOTH SETUP
        final Intent intent = getIntent();
        device_name = intent.getStringExtra("device_name");
        device_address = intent.getStringExtra("device_address");

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //Poller setup
        bluetoothServiceManager.addCharacteristicUUID(Globals.TEMP_CHAR, Globals.TEMPERATURE_CHARACTERISTIC_UUID);
        bluetoothServiceManager.addCharacteristicUUID(Globals.PITCH_CHAR, Globals.PITCH_CHARACTERISTIC_UUID);
        bluetoothServiceManager.addCharacteristicUUID(Globals.ROLL_CHAR, Globals.ROLL_CHARACTERISTIC_UUID);

        bluetoothServiceManager.addCharacteristicUUID(Globals.TEST_CHAR, Globals.TEST_CHARACTERISTIC_UUID);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                Log.e("Bluetooth connection", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            boolean success = bluetoothLeService.connect(device_address);
            Log.w("success", "" + success);
            if (!success) {
                bluetoothLeService.disconnect();
                disconnectedFromTarget();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bluetoothLeService != null) {
            final boolean result = bluetoothLeService.connect(device_address);
            if (!result){
                bluetoothLeService.disconnect();
                disconnectedFromTarget();
            }
        }
    }

    private void initServices(){
        List<BluetoothGattService> services = bluetoothLeService.getSupportedGattServices();
        for (BluetoothGattService s : services){
            if (bluetoothServiceManager.isServiceUUID(s.getUuid())){
                for (BluetoothGattCharacteristic c : s.getCharacteristics()){
                    bluetoothServiceManager.attemptUUIDLink(c);
                }
            }
        }
    }

    private void startPoller(){
        final Handler h = new Handler();
        pollerEnabled = true;

        h.postDelayed(new Runnable() {
            public void run() {
                pollDeviceRead();
                updateUI();
                if (pollerEnabled) h.postDelayed(this, Globals.POLLER_DELAY);
            }
        }, Globals.POLLER_DELAY);
    }

    private void pollDeviceRead(){
        for (BluetoothGattCharacteristic c : bluetoothServiceManager.getAllCharacteristics()){
            bluetoothLeService.readCharacteristic(c);
        }
    }

    private void updateUI(){
        byte[] bytes = bluetoothServiceManager.getCharacteristic(Globals.TEST_CHAR).getValue();
        if (bytes == null) return;

        short[] shorts = Utils.bytesTotwoBitInt(bytes);

        debug_1.setText("" + shorts[0]);
        debug_2.setText("" + shorts[1]);
        debug_3.setText("" + shorts[2]);
    }

    private void stopPoller(){
        pollerEnabled = false;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
                disconnectedFromTarget();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                initServices();
                startPoller();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            }
        }
    };

    private void disconnectedFromTarget(){
        final Intent intent = new Intent(this, LauncherActivity.class);
        Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to target device", Toast.LENGTH_SHORT);
        toast.show();
        startActivity(intent);
    }
}
