package ax.stardust.skvirrel.component.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

/**
 * EditText which suppresses IME show up and is bound to a specific input and validator-function for validating input of this EditText.
 * <p>
 * This is the same as an native EditText, except that no soft keyboard
 * will appear when user clicks on widget. This is modeled after the keyboard
 * in the default Android KitKat dialer app.
 * Proudly snatched from: https://github.com/danialgoodwin/android-simply-tone-generator/blob/master/app/src/main/java/net/simplyadvanced/simplytonegenerator/widget/KeyboardlessEditText.java
 * <p>
 * As this stuff is copied and pasted(it was a pain to find any good stuff about this on stack overflow) I'm not 100%
 * sure of what every little code snippet are doing. Therefore this is to be used with a bit caution.
 */
@Getter
@Setter
public class KeyboardlessEditText extends AppCompatEditText {

    /**
     * The different types of valid input for a keyboardless edit text.
     */
    public enum Input {
        TEXT,
        NUMERIC_DECIMAL,
        NUMERIC_INTEGER
    }

    private static final Method SHOW_SOFT_INPUT_ON_FOCUS = getMethod(boolean.class);

    private Input input;

    public KeyboardlessEditText(Context context) {
        super(context);
        initialize();
    }

    public KeyboardlessEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public KeyboardlessEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        synchronized (this) {
            setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            setFocusableInTouchMode(true);
        }

        // need to show the cursor when user interacts with EditText so that the edit operations
        // still work. Without the cursor, the edit operations won't appear.
        setOnClickListener(onClickListener);
        setOnLongClickListener(onLongClickListener);

        setShowSoftInputOnFocus(false); // This is a hidden method in TextView.
        reflexSetShowSoftInputOnFocus(); // Workaround.

        // ensure that cursor is at the end of the input box when initialized. Without this, the
        // cursor may be at index 0 when there is text added via layout XML.
        setSelection(Objects.requireNonNull(getText()).length());
    }

    private final View.OnClickListener onClickListener = v -> setCursorVisible(true);

    private final View.OnLongClickListener onLongClickListener = v -> {
        setCursorVisible(true);
        return false;
    };

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        hideKeyboard();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        final boolean ret = super.onTouchEvent(event);

        // must be done after super.onTouchEvent()
        hideKeyboard();
        return ret;
    }

    @Override
    @SuppressWarnings("EmptyMethod")
    public boolean performClick() {
        return super.performClick();
    }

    private void hideKeyboard() {
        // hide system keyboard
        final InputMethodManager inputMethodManager = ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        if (inputMethodManager != null && inputMethodManager.isActive(this)) {
            inputMethodManager.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
        }
    }

    private void reflexSetShowSoftInputOnFocus() {
        if (SHOW_SOFT_INPUT_ON_FOCUS != null) {
            invokeMethod(this, false);
        } else {
            // use fallback method. Not tested.
            hideKeyboard();
        }
    }

    /**
     * Returns method if available in class or superclass (recursively),
     * otherwise returns null.
     */
    @SuppressWarnings("SameParameterValue")
    private static Method getMethod(Class<?>... parametersType) {
        Class<?> superClass = ((Class<?>) EditText.class).getSuperclass();
        while (superClass != Object.class) {
            try {
                return Objects.requireNonNull(superClass).getDeclaredMethod("setShowSoftInputOnFocus", parametersType);
            } catch (NoSuchMethodException e) {
                // Just super it again
            }
            superClass = Objects.requireNonNull(superClass).getSuperclass();
        }
        return null;
    }

    /**
     * Returns results if available, otherwise returns null.
     */
    @SuppressWarnings("SameParameterValue")
    private static void invokeMethod(Object receiver, Object... args) {
        try {
            Objects.requireNonNull(KeyboardlessEditText.SHOW_SOFT_INPUT_ON_FOCUS).invoke(receiver, args);
        } catch (IllegalArgumentException e) {
            Timber.e(e, "Safe invoke fail - Invalid args");
        } catch (IllegalAccessException e) {
            Timber.e(e, "Safe invoke fail - Invalid access");
        } catch (InvocationTargetException e) {
            Timber.e(e, "Safe invoke fail - Invalid target");
        }
    }
}
