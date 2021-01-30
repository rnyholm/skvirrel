package ax.stardust.skvirrel.component.keyboard;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import org.apache.commons.lang3.StringUtils;

import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;

/**
 * Handler for the application keyboard, handles different user events.
 */
public class KeyboardHandler implements View.OnFocusChangeListener, View.OnTouchListener {
    private final SkvirrelKeyboard skvirrelKeyboard;

    /**
     * Creates a new instance of keyboard handler for given keyboard
     *
     * @param skvirrelKeyboard keyboard which this handler should handle
     */
    public KeyboardHandler(SkvirrelKeyboard skvirrelKeyboard) {
        this.skvirrelKeyboard = skvirrelKeyboard;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (KeyboardlessEditText.class.isAssignableFrom(view.getClass())) {
            KeyboardlessEditText keyboardlessEditText = (KeyboardlessEditText) view;
            if (hasFocus) {
                Editable editable = keyboardlessEditText.getText();
                InputConnection inputConnection = view.onCreateInputConnection(new EditorInfo());
                this.skvirrelKeyboard.show();
                this.skvirrelKeyboard.configureSeparatorButton(keyboardlessEditText.getInput());
                this.skvirrelKeyboard.enableDeleteButton(editable != null && StringUtils.isNotEmpty(editable.toString()));
                this.skvirrelKeyboard.setInputConnection(inputConnection);
            } else {
                this.skvirrelKeyboard.delayedHide();
            }
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (KeyboardlessEditText.class.isAssignableFrom(view.getClass())) {
            if (skvirrelKeyboard.getVisibility() != View.VISIBLE) {
                KeyboardlessEditText keyboardlessEditText = (KeyboardlessEditText) view;
                Editable editable = keyboardlessEditText.getText();
                InputConnection inputConnection = view.onCreateInputConnection(new EditorInfo());
                this.skvirrelKeyboard.show();
                this.skvirrelKeyboard.configureSeparatorButton(keyboardlessEditText.getInput());
                this.skvirrelKeyboard.enableDeleteButton(editable != null && StringUtils.isNotEmpty(editable.toString()));
                this.skvirrelKeyboard.setInputConnection(inputConnection);
            }
        }
        // let the rest of the framework handle this event also
        return false;
    }
}
