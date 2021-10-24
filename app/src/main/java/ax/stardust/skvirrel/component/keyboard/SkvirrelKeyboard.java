package ax.stardust.skvirrel.component.keyboard;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;

/**
 * Custom implementation of a keyboard used within the application.
 */
public abstract class SkvirrelKeyboard extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    private static final int HIDE_DELAY = 25;
    private static final int CONTINUOUS_DELETE_DELAY = 60;

    // common buttons
    protected Button button0;
    protected Button button1;
    protected Button button2;
    protected Button button3;
    protected Button button4;
    protected Button button5;
    protected Button button6;
    protected Button button7;
    protected Button button8;
    protected Button button9;
    protected Button buttonDelete;

    protected final SparseArray<String> keyValues = new SparseArray<>();

    private InputConnection inputConnection;

    private final Handler hideDelayHandler = new Handler();
    private final Handler continuousDeleteHandler = new Handler();
    private final Runnable continuousDeleteAction = new Runnable() {
        @Override
        public void run() {
            actionDelete();
            continuousDeleteHandler.postDelayed(continuousDeleteAction, CONTINUOUS_DELETE_DELAY);
        }
    };

    /**
     * Creates a new instance of skvirrel keyboard with given context
     *
     * @param context context for keyboard
     */
    public SkvirrelKeyboard(Context context) {
        this(context, null, 0);
    }

    /**
     * Creates a new instance of skvirrel keyboard with given context and attribute set
     *
     * @param context context for keyboard
     * @param attrs   attribute set for keyboard
     */
    public SkvirrelKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a new instance of skvirrel keyboard with given context, attribute set and style attribute
     *
     * @param context      context for keyboard
     * @param attrs        attribute set for keyboard
     * @param defStyleAttr style attribute
     */
    public SkvirrelKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflateLayout(context);
        findViews();
        setListeners();
        setKeyValues();
    }

    /**
     * Inflates keyboard layout from context
     *
     * @param context context from which layout is inflated
     */
    protected abstract void inflateLayout(Context context);

    /**
     * Find views needed for the keyboard
     */
    protected abstract void findViews();

    /**
     * Set listeners needed for the keyboard
     */
    protected abstract void setListeners();

    /**
     * Set keyboard key(button values)
     */
    protected abstract void setKeyValues();

    /**
     * To configure separator character button depending on type of input, example usage
     * could be to hide the button depending on input type
     *
     * @param input input type for which separator character button is configured
     */
    public abstract void configureSeparatorButton(KeyboardlessEditText.Input input);

    @Override
    public boolean onLongClick(View view) {
        if (inputConnection != null) {
            if (view != null) {
                if (isDeleteButton(view)) {
                    continuousDeleteHandler.post(continuousDeleteAction);
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (inputConnection != null) {
            if (isDeleteButton(view)) {
                continuousDeleteHandler.removeCallbacks(continuousDeleteAction);
                actionDelete();
            } else {
                String keyValue = keyValues.get(view.getId());
                inputConnection.commitText(keyValue, 1);
            }
        }
    }

    /**
     * Sets input connection to this keyboard
     *
     * @param inputConnection input connection of this keyboard
     */
    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    /**
     * Enables/disables delete button of this keyboard depending on parameter
     *
     * @param enable true if delete button should be enabled else false
     */
    public void enableDeleteButton(boolean enable) {
        buttonDelete.setEnabled(enable);
        // Nothing more to delete in field
        continuousDeleteHandler.removeCallbacks(continuousDeleteAction);
    }

    /**
     * To make this keyboard visible
     */
    public void show() {
        hideDelayHandler.removeCallbacksAndMessages(null);
        setVisibility(View.VISIBLE);
    }

    /**
     * To hide the keyboard with a small delay
     */
    public void delayedHide() {
        hideDelayHandler.postDelayed(this::hide, HIDE_DELAY);
    }

    private boolean isDeleteButton(View view) {
        return R.id.numeric_keyboard_button_del == view.getId() ||
                R.id.alphanumeric_keyboard_button_del == view.getId();
    }

    private void hide() {
        setVisibility(View.GONE);
    }

    /**
     * DON'T EVER, EVER call this before you have checked that:
     * - inputConnection is not null
     * - calling method has a view that's not null
     * - calling method has a view with id: R.id.button_del
     */
    private void actionDelete() {
        CharSequence selectedText = inputConnection.getSelectedText(0);
        if (StringUtils.isEmpty(selectedText)) {
            inputConnection.deleteSurroundingText(1, 0);
        } else {
            inputConnection.commitText("", 1);
        }
    }
}