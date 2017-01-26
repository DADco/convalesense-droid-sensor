package co.dad.convalesensechild;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String BT_NAME = "Convalesense Android";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothMessagingService mChatService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI

        Button btnGame1 = (Button) findViewById(R.id.button_arm_strength);
        btnGame1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameArmStrength.class);
                startActivity(intent);
            }
        });

        Button btnGame2 = (Button) findViewById(R.id.button_finger_strength);
        btnGame2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameFingerStrength.class);
                startActivity(intent);
            }
        });

        Button btnGame3 = (Button) findViewById(R.id.button_power);
        btnGame3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GamePower.class);
                startActivity(intent);
            }
        });

        //  Comms

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        mBluetoothAdapter.setName(MainActivity.BT_NAME);

        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mChatService = BluetoothMessagingService.getInstance(mHandler);
        mChatService.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // Bluetooth

    private String mConnectedDeviceName;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothMessagingService.STATE_CONNECTED:
                            Log.d("tag", "try to connect to :" + mConnectedDeviceName);
                            break;
                        case BluetoothMessagingService.STATE_CONNECTING:
                            Log.d("tag", "connecting");
                            break;
                        case BluetoothMessagingService.STATE_LISTEN:
                        case BluetoothMessagingService.STATE_NONE:
                            Log.d("tag", "connected");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d("tag", writeMessage);
                    //Toast.makeText(MainActivity.this, writeMessage, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("tag", readMessage);
                    //Toast.makeText(MainActivity.this, readMessage, Toast.LENGTH_LONG).show();
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
