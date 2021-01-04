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

    public SkvirrelKeyboard(Context context) {
        this(context, null, 0);
    }

    public SkvirrelKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

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

    protected abstract void inflateLayout(Context context);

    protected abstract void findViews();

    protected abstract void setListeners();

    protected abstract void setKeyValues();

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

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public void enableDeleteButton(boolean enable) {
        buttonDelete.setEnabled(enable);
        // Nothing more to delete in field
        continuousDeleteHandler.removeCallbacks(continuousDeleteAction);
    }

    public void show() {
        hideDelayHandler.removeCallbacksAndMessages(null);
        setVisibility(View.VISIBLE);
    }

    public void delayedHide() {
        hideDelayHandler.postDelayed(this::hide, HIDE_DELAY);
    }

    private boolean isDeleteButton(View view) {
        return R.id.button_del == view.getId() ||
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