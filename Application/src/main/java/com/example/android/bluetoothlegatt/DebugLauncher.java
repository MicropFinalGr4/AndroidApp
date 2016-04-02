package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DebugLauncher extends Activity {

    TextView leftText;
    int debug_int = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_launcher);
        leftText = (TextView) findViewById(R.id.dl_tv);
    }

    public void launcherButtonHandler(View v){
        final Intent intent;
        switch (v.getId()){
            case R.id.dl_scan_btn:
                intent = new Intent(this, DeviceScanActivity.class);
                startActivity(intent);
                break;
            case R.id.dl_cv_btn:
                intent = new Intent(this, ConnectedDevice.class);
                startActivity(intent);
                break;
            case R.id.dl_ml_btn:
                intent = new Intent(this, LauncherActivity.class);
                startActivity(intent);
                break;
        }
    }

//    final Intent intent = new Intent(this, DebugLauncher.class);
//    if (mScanning) {
//        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        mScanning = false;
//    }
//    startActivity(intent);
}
