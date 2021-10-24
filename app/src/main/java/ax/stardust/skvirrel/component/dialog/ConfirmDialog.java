package ax.stardust.skvirrel.component.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ax.stardust.skvirrel.R;
import lombok.RequiredArgsConstructor;
import timber.log.Timber;

/**
 * A simple confirm dialog with title, message and a positive and negative button.
 */
@RequiredArgsConstructor
public class ConfirmDialog extends DialogFragment {

    public static final String FRAGMENT_TAG = ConfirmDialog.class.getSimpleName();

    private DialogInteractionListener callback;

    private final String title;
    private final String message;
    private final String positiveAnswer;
    private final String negativeAnswer;

    private TextView titleTextView;
    private TextView messageTextView;

    private Button positiveButton;
    private Button negativeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // try to find callback on creation
            callback = (DialogInteractionListener) getTargetFragment();
        } catch (Exception e) {
            IllegalStateException exception = new IllegalStateException("No target fragment set for "
                    + "dialog which is mandatory");
            Timber.e(exception, "Unable to create dialog");
            throw exception;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.confirm_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setTexts();
        setListeners();
    }

    private void findViews(View view) {
        titleTextView = view.findViewById(R.id.confirm_title_tv);
        messageTextView = view.findViewById(R.id.confirm_message_tv);
        positiveButton = view.findViewById(R.id.confirm_positive_btn);
        negativeButton = view.findViewById(R.id.confirm_negative_btn);
    }

    private void setTexts() {
        titleTextView.setText(title);
        messageTextView.setText(message);
        positiveButton.setText(positiveAnswer);
        negativeButton.setText(negativeAnswer);
    }

    private void setListeners() {
        positiveButton.setOnClickListener(view -> callback.onPositiveButtonPressed());
        negativeButton.setOnClickListener(view -> callback.onNegativeButtonPressed());
    }
}
