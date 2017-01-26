package co.dad.convalesensechild;

import android.os.Bundle;
import android.widget.TextView;

public class GameFingerStrength extends GameBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finger_strength);

        // UI

        TextView textView = (TextView) findViewById(R.id.instruction);
        textView.setText(R.string.instruction_finger_strength);
    }
}
