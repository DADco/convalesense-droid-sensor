package co.dad.convalesensechild;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
public class GameBase extends AppCompatActivity {

    protected BluetoothMessagingService mChatService;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Log.d("tag", writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("tag", readMessage);
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        mChatService = BluetoothMessagingService.getInstance(mHandler);
        mChatService.start();
    }

    /**
     * Send a score to the data channel.
     * @param score
     */
    protected void sendScore(int score) {

        final String message = "" + score;

        if (mChatService != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mChatService.write(message.getBytes());
                }
            }).start();
        }
    }
}
