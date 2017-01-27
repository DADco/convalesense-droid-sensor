package co.dad.convalesensechild;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
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

class GameArmStrengthResult {

    private long lastScoreTaken = System.currentTimeMillis();
    private boolean wasHorizontal = false;

    public boolean isScoreable() {
        long dt = System.currentTimeMillis() - lastScoreTaken;
        return dt > 2000 && wasHorizontal;
    }

    public void score() {
        this.lastScoreTaken = System.currentTimeMillis();
        wasHorizontal = false;
    }

    public void setWasHorizontal(boolean wasHorizontal) {
        this.wasHorizontal = wasHorizontal;
    }
}

/**
 * Game to train arm strength through angular rotation of arm.
 */
public class GameArmStrength extends GameBase {

    static String TAG = "GameArmStrength";

    private Subscription subAccelero;

    /** Rotation Matrix */
    final float[] MAG = new float[] {1f, 1f, 1f};
    final float[] I = new float[16];
    final float[] RR = new float[16];
    final float[] outR = new float[16];
    final float[] LOC = new float[3];

    final GameArmStrengthResult result = new GameArmStrengthResult();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_arm_strength);

        // UI

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getDrawable();
        frameAnimation.start();

        TextView textView = (TextView) findViewById(R.id.instruction);
        textView.setText(R.string.instruction_arm_strength);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void stopSensors() {
        if (subAccelero != null) {
            subAccelero.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSensors();
    }

    private void setupSensors() {
        ReactiveSensors sensors = new ReactiveSensors(this);

        if (sensors.hasSensor(Sensor.TYPE_ACCELEROMETER)) {
            subAccelero = new ReactiveSensors(this).observeSensor(Sensor.TYPE_ACCELEROMETER,
                    SensorManager.SENSOR_DELAY_NORMAL)
                    .subscribeOn(Schedulers.computation())
                    .filter(ReactiveSensorFilter.filterSensorChanged())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ReactiveSensorEvent>() {
                        @Override
                        public void call(ReactiveSensorEvent reactiveSensorEvent) {

                            SensorEvent event = reactiveSensorEvent.getSensorEvent();

                            int displayOrientation = getWindowManager().getDefaultDisplay().getRotation();

                            SensorManager.getRotationMatrix(RR, I, event.values, MAG);

                            // compute pitch, roll & balance
                            switch (displayOrientation) {
                                case Surface.ROTATION_270:
                                    SensorManager.remapCoordinateSystem(
                                            RR,
                                            SensorManager.AXIS_MINUS_Y,
                                            SensorManager.AXIS_X,
                                            outR);
                                    break;
                                case Surface.ROTATION_180:
                                    SensorManager.remapCoordinateSystem(
                                            RR,
                                            SensorManager.AXIS_MINUS_X,
                                            SensorManager.AXIS_MINUS_Y,
                                            outR);
                                    break;
                                case Surface.ROTATION_90:
                                    SensorManager.remapCoordinateSystem(
                                            RR,
                                            SensorManager.AXIS_Y,
                                            SensorManager.AXIS_MINUS_X,
                                            outR);
                                    break;
                                case Surface.ROTATION_0:
                                default:
                                    SensorManager.remapCoordinateSystem(
                                            RR,
                                            SensorManager.AXIS_X,
                                            SensorManager.AXIS_Y,
                                            outR);
                                    break;
                            }

                            SensorManager.getOrientation(outR, LOC);

                            // normalize z on ux, uy
                            float tmp = (float) Math.sqrt(outR[8] * outR[8] + outR[9] * outR[9]);
                            tmp = (tmp == 0 ? 0 : outR[8] / tmp);

                            // LOC[0] compass
                            float pitch = (float) Math.toDegrees(LOC[1]);
                            float roll = -(float) Math.toDegrees(LOC[2]);
                            float degs = (float) Math.abs(Math.toDegrees(Math.asin(tmp)));

                            //Log.d("ARMSTRENGTH", "degs=" + degs);

                            // At 0 the arm has been bent up veritcally
                            if (result.isScoreable() && degs < 10) {
                                synchronized (result) {
                                    Log.d(TAG, "score");
                                    result.score();
                                    sendScore(1);
                                }
                            }

                            // At 50 degrees set horizontal flag - a rep has been done
                            if (degs > 50) {
                                result.setWasHorizontal(true);
                            }
                        }
                    });
        }
    }
}
