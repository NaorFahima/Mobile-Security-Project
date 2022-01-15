package com.example.mobilesecurityproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView tv_accelerometer_values ,tv_rotation_values ,tv_magnetometer_values;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            HashMap<String, String> data = (HashMap<String, String>)intent.getSerializableExtra("data");
            Log.d("DATA-MAIN", data.get("accelerometer-x"));
            tv_accelerometer_values.setText("Accelerometer:\nX: " + data.get("accelerometer-x") + "\nY: " + data.get("accelerometer-y") + "\nZ: " + data.get("accelerometer-z"));
            tv_rotation_values.setText("Rotation:\nX: " + data.get("orientation-x") + "\nY: " +  data.get("orientation-y") + "\nZ: " + data.get("orientation-z"));
            tv_magnetometer_values.setText("Magnetometer:\nX: " + data.get("magnetometer-x") + "\nY: " +  data.get("magnetometer-y") + "\nZ: " + data.get("magnetometer-z"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Intent intent = new Intent(this, SensorService.class);
        startService(intent);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("intentKey"));

    }

    public void initView() {
        tv_accelerometer_values = findViewById(R.id.accelerometer_values);
        tv_rotation_values = findViewById(R.id.rotation_values);
        tv_magnetometer_values = findViewById(R.id.magnetometer_values);
    }

}