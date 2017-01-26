package co.dad.convalesensechild;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GamePower extends GameBase {

    private Subscription subLinearAcceleration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_power);

        // UI

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

                            if (dx > threshold && dt > punchThreshold) {
                                Log.d("EVENT", "DETECT");
                                last.t = t;
                            }

                            last.x = x;
                            last.y = y;
                            last.z = z;
                        }
                    });

        }
    }
}
