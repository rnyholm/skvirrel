package ax.stardust.skvirrel.component.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;

/**
 * Numeric keyboard implementation of the skvirrel keyboard.
 */
public class NumericKeyboard extends SkvirrelKeyboard {
    // the only difference from a skvirrel "base" keyboard :(
    private Button buttonDot;

    /**
     * Creates a new instance of numeric keyboard with given context
     *
     * @param context context for keyboard
     */
    public NumericKeyboard(Context context) {
        super(context);
    }

    /**
     * Creates a new instance of numeric keyboard with given context and attribute set
     *
     * @param context context for keyboard
     * @param attrs   attribute set for keyboard
     */
    public NumericKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a new instance of numeric keyboard with given context, attribute set and style attribute
     *
     * @param context      context for keyboard
     * @param attrs        attribute set for keyboard
     * @param defStyleAttr style attribute
     */
    public NumericKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflateLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.numeric_keyboard, this, true);
    }

    @Override
    protected void findViews() {
        button0 = findViewById(R.id.numeric_keyboard_button_0);
        button1 = findViewById(R.id.numeric_keyboard_button_1);
        button2 = findViewById(R.id.numeric_keyboard_button_2);
        button3 = findViewById(R.id.numeric_keyboard_button_3);
        button4 = findViewById(R.id.numeric_keyboard_button_4);
        button5 = findViewById(R.id.numeric_keyboard_button_5);
        button6 = findViewById(R.id.numeric_keyboard_button_6);
        button7 = findViewById(R.id.numeric_keyboard_button_7);
        button8 = findViewById(R.id.numeric_keyboard_button_8);
        button9 = findViewById(R.id.numeric_keyboard_button_9);
        buttonDot = findViewById(R.id.numeric_keyboard_button_dot);
        buttonDelete = findViewById(R.id.numeric_keyboard_button_del);
    }

    @Override
    protected void setListeners() {
        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonDot.setOnClickListener(this);
        // delete button needs this many listeners in order to handle long press delete
        buttonDelete.setOnClickListener(this);
        buttonDelete.setOnLongClickListener(this);
    }

    @Override
    protected void setKeyValues() {
        keyValues.put(R.id.numeric_keyboard_button_0, "0");
        keyValues.put(R.id.numeric_keyboard_button_1, "1");
        keyValues.put(R.id.numeric_keyboard_button_2, "2");
        keyValues.put(R.id.numeric_keyboard_button_3, "3");
        keyValues.put(R.id.numeric_keyboard_button_4, "4");
        keyValues.put(R.id.numeric_keyboard_button_5, "5");
        keyValues.put(R.id.numeric_keyboard_button_6, "6");
        keyValues.put(R.id.numeric_keyboard_button_7, "7");
        keyValues.put(R.id.numeric_keyboard_button_8, "8");
        keyValues.put(R.id.numeric_keyboard_button_9, "9");
        keyValues.put(R.id.numeric_keyboard_button_dot, ".");
    }

    @Override
    public void configureSeparatorButton(KeyboardlessEditText.Input input) {
        // hide separator button if numeric integer input
        if (KeyboardlessEditText.Input.NUMERIC_INTEGER.equals(input)) {
            buttonDot.setVisibility(INVISIBLE);
        } else {
            buttonDot.setVisibility(VISIBLE);
        }
    }
}
