package com.example.mobilesecurityproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.HashMap;

public class SensorService extends Service implements SensorEventListener {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor ,mRotationVectorSensor ,magnetometerSensor;
    private HashMap <String, String> data;

    private float[] mValuesMagnet      = new float[3];
    private float[] mValuesAccel       = new float[3];
    private float[] mValuesOrientation = new float[3];

    private float[] mRotationMatrix    = new float[9];

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x,y,z;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                data.put("accelerometer-x", String.valueOf(x));
                data.put("accelerometer-y", String.valueOf(y));
                data.put("accelerometer-z", String.valueOf(z));

                // For debug
                Log.d("DATA:accelerometer-x", data.get("accelerometer-x"));
                Log.d("DATA:accelerometer-y", data.get("accelerometer-y"));
                Log.d("DATA:accelerometer-z", data.get("accelerometer-z"));

                System.arraycopy(event.values, 0, mValuesAccel, 0, 3);

                break;

            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
                SensorManager.getOrientation(mRotationMatrix, mValuesOrientation);

                data.put("orientation-x", String.valueOf(mValuesOrientation[1]));
                data.put("orientation-y", String.valueOf(mValuesOrientation[2]));
                data.put("orientation-z", String.valueOf(mValuesOrientation[0]));

                // For debug
                Log.d("DATA:orientation-x", data.get("orientation-x"));
                Log.d("DATA:orientation-y", data.get("orientation-y"));
                Log.d("DATA:orientation-z", data.get("orientation-z"));
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                data.put("magnetometer-x", String.valueOf(x));
                data.put("magnetometer-y", String.valueOf(y));
                data.put("magnetometer-z", String.valueOf(z));

                // For debug
                Log.d("DATA:magnetometer-x", data.get("magnetometer-x"));
                Log.d("DATA:magnetometer-y", data.get("magnetometer-y"));
                Log.d("DATA:magnetometer-z", data.get("magnetometer-z"));

                System.arraycopy(event.values, 0, mValuesMagnet, 0, 3);

            default:
                break;

        }

        sendDataToMainActivity();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);

        thread.start();

        data = new HashMap<>();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI );

        mRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_UI );

        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_UI );

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
    }

    private void sendDataToMainActivity() {
        Intent intent = new Intent("intentKey");
        // You can also include some extra data.
        intent.putExtra("data", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}