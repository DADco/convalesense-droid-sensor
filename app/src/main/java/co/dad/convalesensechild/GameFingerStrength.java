package co.dad.convalesensechild;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GameFingerStrength extends GameBase {

    static String TAG = "GameFingerStrength";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finger_strength);

        // UI

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
        frameAnimation.start();

        TextView textView = (TextView) findViewById(R.id.instruction);
        textView.setText(R.string.instruction_finger_strength);

        View view = (View) findViewById(R.id.activity_game_finger_strength);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "score");
                sendScore(1);
                return false;
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void stopSensors() {

    }
}
