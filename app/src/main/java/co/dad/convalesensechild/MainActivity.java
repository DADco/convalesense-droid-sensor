package co.dad.convalesensechild;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Subscription subGyro;
    private Subscription subAccelero;
    private Subscription subStepCounter;
    private Subscription subStepDetector;
    private Subscription subLinearAcceleration;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }


        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSensors();
    }

    private void setupSensors() {
        ReactiveSensors sensors = new ReactiveSensors(this);

        if (sensors.hasSensor(Sensor.TYPE_GYROSCOPE)) {
            subGyro = new ReactiveSensors(this).observeSensor(Sensor.TYPE_GYROSCOPE)
                    .subscribeOn(Schedulers.computation())
                    .filter(ReactiveSensorFilter.filterSensorChanged())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ReactiveSensorEvent>() {
                        @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                            SensorEvent event = reactiveSensorEvent.getSensorEvent();

                            float x = event.values[0];
                            float y = event.values[1];
                            float z = event.values[2];

                            String message = String.format("Gyroscope x = %f, y = %f, z = %f", x, y, z);
                            //Log.d("gyroscope readings", message);

                            if (mChatService != null) {
                                mChatService.write(message.getBytes());
                            }

                        }
                    });
        }

        if (sensors.hasSensor(Sensor.TYPE_LINEAR_ACCELERATION)) {
            subLinearAcceleration = new ReactiveSensors(this).observeSensor(Sensor.TYPE_LINEAR_ACCELERATION)
                    .subscribeOn(Schedulers.computation())
                    .filter(ReactiveSensorFilter.filterSensorChanged())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ReactiveSensorEvent>() {
                        @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                            SensorEvent event = reactiveSensorEvent.getSensorEvent();

                            float x = event.values[0];
                            float y = event.values[1];
                            float z = event.values[2];

                            String message = String.format("Linear Acceleration x = %f, y = %f, z = %f", x, y, z);
                            Log.d("la readings", message);

                            if (mChatService != null) {
                                mChatService.write(message.getBytes());
                            }

                        }
                    });
        }


        if (sensors.hasSensor(Sensor.TYPE_ACCELEROMETER)) {
            subAccelero = new ReactiveSensors(this).observeSensor(Sensor.TYPE_ACCELEROMETER)
                    .subscribeOn(Schedulers.computation())
                    .filter(ReactiveSensorFilter.filterSensorChanged())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ReactiveSensorEvent>() {
                        @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                            SensorEvent event = reactiveSensorEvent.getSensorEvent();

                            float x = event.values[0];
                            float y = event.values[1];
                            float z = event.values[2];

                            String message = String.format("Accelerometer x = %f, y = %f, z = %f", x, y, z);
                            //Log.d("gyroscope readings", message);
                            if (mChatService != null) {
                                mChatService.write(message.getBytes());
                            }

                        }
                    });
        }


        if (sensors.hasSensor(Sensor.TYPE_STEP_COUNTER)) {
            subStepCounter = new ReactiveSensors(this).observeSensor(Sensor.TYPE_STEP_COUNTER)
                    .subscribeOn(Schedulers.computation())
                    .filter(ReactiveSensorFilter.filterSensorChanged())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ReactiveSensorEvent>() {
                        @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                            SensorEvent event = reactiveSensorEvent.getSensorEvent();

                            float x = event.values[0];
                            float y = event.values[1];
                            float z = event.values[2];

                            String message = String.format("step counter x = %f, y = %f, z = %f", x, y, z);
                            //Log.d("gyroscope readings", message);
                            if (mChatService != null) {
                                mChatService.write(message.getBytes());
                            }

                        }
                    });

        }

        if (sensors.hasSensor(Sensor.TYPE_STEP_DETECTOR)) {
            subStepDetector = new ReactiveSensors(this).observeSensor(Sensor.TYPE_STEP_DETECTOR)
                    .subscribeOn(Schedulers.computation())
                    .filter(ReactiveSensorFilter.filterSensorChanged())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ReactiveSensorEvent>() {
                        @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                            SensorEvent event = reactiveSensorEvent.getSensorEvent();

                            float x = event.values[0];
                            float y = event.values[1];
                            float z = event.values[2];

                            String message = String.format("step detector x = %f, y = %f, z = %f", x, y, z);
                            //Log.d("gyroscope readings", message);
                            if (mChatService != null) {
                                mChatService.write(message.getBytes());
                            }

                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mChatService = new BluetoothChatService(this, mHandler);
        mChatService.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (subAccelero != null) {
            subAccelero.unsubscribe();
        }

        if (subGyro != null) {
            subGyro.unsubscribe();
        }

        if (subStepCounter != null) {
            subStepCounter.unsubscribe();
        }

        if (subStepDetector != null) {
            subStepDetector.unsubscribe();
        }

        if (subLinearAcceleration != null) {
            subLinearAcceleration.unsubscribe();
        }
    }

    private void connectDevice(String deviceHardwareAddress) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceHardwareAddress);
        mBluetoothAdapter.cancelDiscovery();
        mChatService.connect(device, true);
    }

    private String mConnectedDeviceName;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.d("tag", "try to connect to :" + mConnectedDeviceName);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.d("tag", "connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            Log.d("tag", "connected");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d("tag", writeMessage);
                    Toast.makeText(MainActivity.this, writeMessage, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("tag", readMessage);
                    Toast.makeText(MainActivity.this, readMessage, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Build.DEVICE);

                    Toast.makeText(MainActivity.this, "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
