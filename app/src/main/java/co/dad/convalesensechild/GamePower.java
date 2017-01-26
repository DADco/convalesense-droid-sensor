package co.dad.convalesensechild;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

class GamePowerResult {

    private long lastScoreTaken;

    public boolean isScoreable() {
        long dt = System.currentTimeMillis() - lastScoreTaken;
        return dt > 2000;
    }

    public void score() {
        this.lastScoreTaken = System.currentTimeMillis();
    }
}

public class GamePower extends GameBase {

    static String TAG = "GamePower";

    private Subscription subLinearAcceleration;

    final GamePowerResult result = new GamePowerResult();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_power);

        // UI

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
        frameAnimation.start();

        TextView textView = (TextView) findViewById(R.id.instruction);
        textView.setText(R.string.instruction_power);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subLinearAcceleration != null) {
            subLinearAcceleration.unsubscribe();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setupSensors() {
        ReactiveSensors sensors = new ReactiveSensors(this);

        final float threshold = 6;
        final float punchThreshold = 3 * 1000;
        final LastSensorData last = new LastSensorData();

        if (sensors.hasSensor(Sensor.TYPE_LINEAR_ACCELERATION)) {
            subLinearAcceleration = new ReactiveSensors(this).observeSensor(Sensor.TYPE_LINEAR_ACCELERATION, SensorManager.SENSOR_DELAY_NORMAL)
                    .subscribeOn(Schedulers.computation())
                    .filter(ReactiveSensorFilter.filterSensorChanged())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ReactiveSensorEvent>() {
                        @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                            SensorEvent event = reactiveSensorEvent.getSensorEvent();

                            float x = event.values[0];
                            float y = event.values[1];
                            float z = event.values[2];
                            long t = System.currentTimeMillis();

                            float dx = Math.abs(last.x - x);
                            float dy = Math.abs(last.y - y);
                            float dz = Math.abs(last.z - z);
                            long dt = t - last.t;

                            String message = String.format("x = %f, y = %f, z = %f, dx = %f, dy = %f, dz = %f",
                                    x, y, z, dx, dy, dz);
                            // Log.d("SENSOR", message);

                            if (result.isScoreable() && dx > threshold) {
                                Log.d(TAG, "score");
                                result.score();
                                sendScore(1);
                            }

                            last.x = x;
                            last.y = y;
                            last.z = z;
                        }
                    });

        }
    }
}
