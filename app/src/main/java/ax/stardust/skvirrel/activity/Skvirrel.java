package ax.stardust.skvirrel.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.keyboard.KeyboardHandler;
import ax.stardust.skvirrel.component.keyboard.SkvirrelKeyboard;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;

public class Skvirrel extends AppCompatActivity {
    private SkvirrelKeyboard skvirrelKeyboard;

    private TextView versionNameTextView;

    private KeyboardlessEditText tickerEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skvirrel);
        findViews();
    }

    @Override
    public void onBackPressed() {
        if (skvirrelKeyboard.getVisibility() == View.VISIBLE) {
            // hacky way to release focus from any edit text, by releasing it also the keyboard will be closed
            versionNameTextView.requestFocus();
        } else {
            super.onBackPressed();
        }
    }

    private void findViews() {
        skvirrelKeyboard = findViewById(R.id.soft_keyboard);

        versionNameTextView = findViewById(R.id.version_name_tv);

        tickerEditText = findViewById(R.id.ticker_et);
        tickerEditText.setOnFocusChangeListener(new KeyboardHandler(skvirrelKeyboard));
        tickerEditText.setOnTouchListener(new KeyboardHandler(skvirrelKeyboard));
    }
}