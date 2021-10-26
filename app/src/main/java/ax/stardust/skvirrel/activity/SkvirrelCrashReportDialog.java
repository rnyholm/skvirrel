package ax.stardust.skvirrel.activity;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.acra.ACRA;
import org.acra.dialog.CrashReportDialogHelper;
import org.acra.prefs.SharedPreferencesFactory;

import ax.stardust.skvirrel.R;

/**
 * Dialog activity for getting permission from user to send crash report.
 */
public class SkvirrelCrashReportDialog extends AppCompatActivity {

    private TextView titleTextView;
    private TextView messageTextView;

    private Button positiveButton;
    private Button negativeButton;

    // acra crash report helpers
    private CrashReportDialogHelper helper;
    private SharedPreferencesFactory factory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // some customisation to get an activity look like a dialog, order are important
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_dialog);
        setFinishOnTouchOutside(false); // prevent dialog from being closed by pressing outside of it

        getAcraHelpers();
        findViews();
        setTexts();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (helper != null) {
            helper.cancelReports();
        }
    }

    private void getAcraHelpers() {
        // get acra helpers and shared preferences factory from context
        helper = new CrashReportDialogHelper(this, getIntent());
        factory = new SharedPreferencesFactory(getApplicationContext(), helper.getConfig());
    }

    private void findViews() {
        titleTextView = findViewById(R.id.confirm_title_tv);
        messageTextView = findViewById(R.id.confirm_message_tv);
        positiveButton = findViewById(R.id.confirm_positive_btn);
        negativeButton = findViewById(R.id.confirm_negative_btn);
    }

    private void setTexts() {
        titleTextView.setText(R.string.crash_report_dialog_title);
        messageTextView.setText(R.string.crash_report_dialog_message);
        positiveButton.setText(R.string.crash_report_dialog_send);
        negativeButton.setText(R.string.crash_report_dialog_cancel);
    }

    private void setListeners() {
        positiveButton.setOnClickListener(view -> {
            // get email to send crash report to from shared preferences, default to skvirrel
            // email if no preferences are found(should not be the case though)
            String email = factory.create()
                    .getString(ACRA.PREF_USER_EMAIL_ADDRESS, getString(R.string.skvirrel_email));
            helper.sendCrash("", email);
            finish();
        });

        negativeButton.setOnClickListener(view -> {
            helper.cancelReports();
            finish();
        });
    }
}
