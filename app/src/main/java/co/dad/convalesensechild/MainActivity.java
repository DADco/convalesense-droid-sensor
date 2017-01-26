package co.dad.convalesensechild;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.UUID;

import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;

public class MainActivity extends AppCompatActivity {

    public static String BT_NAME = "Convalesense Android";
    private BluetoothController mBTController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BluetoothAdapter.getDefaultAdapter().setName(BT_NAME);

        mBTController = BluetoothController.getInstance().build(this);
        mBTController.setDiscoverable(300);
        mBTController.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onReadData(BluetoothDevice device, byte[] data) {
                String readMessage = new String(data);
                Log.d("tag", readMessage.trim());
                Intent intent;
                switch (Integer.parseInt(readMessage.trim())) {
                    case 1:
                        intent = new Intent(MainActivity.this, GameArmStrength.class);
                        intent.putExtra("device", device.getAddress());
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, GameFingerStrength.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, GamePower.class);
                        startActivity(intent);
                        break;

                }
            }

            @Override
            public void onActionStateChanged(int preState, int state) {
                Log.d("tag", "onActionStateChanged "+state);
            }

            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {

            }

            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {

            }

            @Override
            public void onBluetoothServiceStateChanged(int state) {
                Log.d("tag", "onBluetoothServiceStateChanged "+state);
            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device, short rssi) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        int fineLocPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int fineCoarsePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fineCoarsePerm != PackageManager.PERMISSION_GRANTED
                || fineLocPerm != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

        } else {
            mBTController.startAsServer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            mBTController.startAsServer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
