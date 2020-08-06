package ax.stardust.skvirrel.component.watcher;

import android.text.Editable;
import android.text.TextWatcher;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.keyboard.SkvirrelKeyboard;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;

public class ReferencedTextWatcher implements TextWatcher {
    private final KeyboardlessEditText input;
    private final SkvirrelKeyboard keyboard;

    public ReferencedTextWatcher(KeyboardlessEditText input, SkvirrelKeyboard keyboard) {
        this.input = input;
        this.keyboard = keyboard;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // do nothing..
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String inputText = charSequence.toString();
        if (!inputText.isEmpty()) {
            try {
                // do some validation or something, throw exception if something fails
                input.setBackgroundResource(R.drawable.input_default);
            } catch (Exception e) {
                // ignore and just set edit-text error color
                input.setBackgroundResource(R.drawable.input_error);
            }
        }

        keyboard.enableDeleteButton(!inputText.isEmpty());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // do nothing..
    }
}
