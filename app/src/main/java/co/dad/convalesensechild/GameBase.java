package co.dad.convalesensechild;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

class LastSensorData {
    long t = System.currentTimeMillis();
    float x = 0;
    float y = 0;
    float z = 0;
}

/**
 * Game base class.
 * Created by alli on 26/01/2017.
 */
public abstract class GameBase extends AppCompatActivity {

    private BluetoothController mBTController;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBTController = BluetoothController.getInstance();

        mBTController.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onReadData(BluetoothDevice device, byte[] data) {
                String readMessage = new String(data);
                Log.d("tag", readMessage.trim());

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
                if (state == State.STATE_DISCONNECTED) {
                    mBTController.release();
                    stopSensors();
                    finish();
                }
            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device, short rssi) {

            }
        });
    }

    /**
     * Send a score to the data channel.
     * @param score
     */
    protected void sendScore(int score) {

        final String message = "" + score;

        if (mBTController != null &&
                mBTController.getConnectedDevice() != null) {
            Log.d("tag", "send data : "+message);
            mBTController.write(message.getBytes());
        }
    }

    public abstract void stopSensors();
}
